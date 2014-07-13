<?php
class LbsService{

	//索引长度 5位
	protected $index_len = 5;
	protected $feed_cache_time = 10800; //活动的缓存时间

	protected $redis;
	protected $geohash;

	public function __construct(){
		//redis
		$this->redis = Cache::getInstance();
		//geohash
		$this->geohash = service('GeoHash');
	}

	/**
	 * 更新用户信息
	 *
	 * @param mixed $latitude 纬度
	 * @param mixed $longitude 经度
	 */
	public function upUserInfo($user_id,$latitude,$longitude){
		//纬度
		$this->redis->hSet('user_loc_'.$user_id,'la',$latitude);
		//经度
		$this->redis->hSet('user_loc_'.$user_id,'lo',$longitude);
		//Geohash
		$hashdata = $this->geohash->encode($latitude,$longitude);
		$this->redis->hSet('user_loc_'.$user_id,'geo',$hashdata);
		return true;
	}
	
	/**
	 * 获取用户的地理信息
	 * @param unknown $user_id
	 * @return multitype:unknown
	 */
	public function getUserInfo($user_id){
		$ret = array();
		$geo = $this->redis->hGet('user_loc_'.$user_id,'geo');
		if (empty($geo)) {
			return $ret;
		}
		$la = $this->redis->hGet('user_loc_'.$user_id,'la');
		$lo = $this->redis->hGet('user_loc_'.$user_id,'lo');
		$ret['latitude'] = $la;
		$ret['longitude'] = $lo;
		$ret['geohash']	= $geo;
		return $ret;
	}
	
	/**
	 * 添加或更新活动信息，内容，地点及圈子信息
	 * @param int $feed_id
	 * @param double $latitude
	 * @param double $longitude
	 */
	public function upFeedInfo($feed_info = array()){
		if (empty($feed_info)) {
			return;
		}
		$feed_id = intval($feed_info['feed_id']);
		$is_secret = $feed_info['is_secret'] == 1 ? true : false;
		unset($feed_info['is_secret']);
		//活动内容缓存
		$this->redis->setex('feed_data_'.$feed_id, $this->feed_cache_time, serialize($feed_info)); // 3小时缓存

		//地理位置信息缓存
		$hashdata = $this->upFeedLoc($feed_id, $feed_info['e_latitude'], $feed_info['e_longitude']);
		if (!empty($hashdata)) {
			if (time() < $feed_info['start_time'] && !$is_secret) { //活动未过期,并且不是私密活动
				$this->upFeedSet($feed_id, $hashdata); //更新圈子
			}
			else{ //活动过期
				$this->remFeedSet($feed_id, $hashdata); //删除圈子
			}
		}
	}
	
	/**
	 * 只更新活动的地理信息包括开始时间,由于活动地点不变，所以这里只是存储删除过程，并无更改。
	 * @param int $feed_id
	 * @param double $latitude
	 * @param double $longitude
	 */
	public function upFeedLoc($feed_id, $latitude, $longitude){
		$arr = array();
		$arr['la'] = $latitude; //纬度
		$arr['lo'] = $longitude; //经度
		$hashdata = $this->geohash->encode($latitude,$longitude);
		$arr['geo'] = $hashdata;
		$this->redis->hMSet('feed_loc_'.$feed_id, $arr);
		$this->redis->setTimeout('feed_loc_'.$feed_id, $this->feed_cache_time); //设置缓存过期时间
		return $hashdata;
	}
	
	/**
	 * 只更新活动的圈子信息，同样活动地点不会变
	 */
	public function upFeedSet($feed_id, $hashdata){
		//索引
		$around_key = substr($hashdata, 0, $this->index_len);
		//存入
		$this->redis->sAdd($around_key, $feed_id);
	}
	
	/**
	 * 删除圈子信息
	 * @param $feed_id
	 * @param $hashdata
	 */
	private function remFeedSet($feed_id, $hashdata){
		//索引
		$around_key = substr($hashdata, 0, $this->index_len);
		//存入
		$this->redis->sRem($around_key, $feed_id);
	}
	
	/**
	 * 删除redis中feed的信息
	 * @param unknown $feed_id
	 * @return boolean
	 */
	public function remFeedInfo($feed_id){
		if (empty($feed_id)) {
			return false;
		}
		$feed_id = intval($feed_id);
		$f_geo = $this->redis->hGet('feed_loc_'.$feed_id,'geo');
		//删除feed的地理位置信息
		if (!empty($f_geo)) {
			$this->redis->delete('feed_loc_'.$feed_id);
		}
		else{
			$f_geo = M('FeedData')->where(array())->getField('e_geohash');
		}
		//删除feed集合中的信息
		if (!empty($f_geo)) {
			$around_key = substr($f_geo, 0, $this->index_len);
			$this->redis->sRem($around_key, $feed_id);
		}
	}

	/**
	 * 获取附近的活动
	 *
	 * @param mixed $latitude 纬度
	 * @param mixed $longitude 经度
	 */
	public function search($latitude, $longitude, $page=1, $limit=10){
		//Geohash
		$hashdata = $this->geohash->encode($latitude,$longitude);
		//索引
		$around_key = substr($hashdata, 0, $this->index_len);
		//取得
		$id_array = $this->redis->sMembers($around_key);
		$id_array_sort = array();
		foreach ($id_array as $id){
			$f_la = $this->redis->hGet('feed_loc_'.$id,'la');
			$f_lo = $this->redis->hGet('feed_loc_'.$id,'lo');
			if(empty($f_la) || empty($f_lo)){
				$data = D('Feed')->getFeedDataDB($id);
				$this->upFeedInfo($data);
				$f_la = $data['e_latitude'];
				$f_lo = $data['e_longitude'];
			}
			$dis = $this->geohash->getDistance($latitude, $longitude, $f_la, $f_lo);
			$id_array_sort[$id] = $dis;
		}
		unset($id_array);
		asort($id_array_sort);
		$offset = $limit * ($page - 1);
		//没有更多数据，返回空
		if ($offset >= count($id_array_sort)) {
			return array();
		}
		return array_slice($id_array_sort, $offset, $limit, true);
	}
	
	/**
	 * 调用geohash的计算距离的方法
	 * @param unknown $lat1
	 * @param unknown $lng1
	 * @param unknown $lat2
	 * @param unknown $lng2
	 */
	public function getDistance($lat1,$lng1,$lat2,$lng2){
		return $this->geohash->getDistance($lat1,$lng1,$lat2,$lng2);
	}

}
