<?php
/**
 * 获取字符串的长度
 * 计算时, 汉字或全角字符占1个长度, 英文字符占0.5个长度
 * @param string  $str
 * @param boolean $filter 是否过滤html标签
 * @return int 字符串的长度
 */
function get_str_length($str, $filter = false){
  if ($filter) {
    $str = html_entity_decode($str, ENT_QUOTES);
    $str = strip_tags($str);
  }
  return (strlen($str) + mb_strlen($str, 'UTF8')) / 4;
}

/**
 * 获取字串首字母
 */
function getFirstLetter($s0) {
  $firstchar_ord = ord(strtoupper($s0{0}));
  if($firstchar_ord >= 65 and $firstchar_ord <= 91) return strtoupper($s0{0});
  if($firstchar_ord >= 48 and $firstchar_ord <= 57) return '#';
  $s = iconv("UTF-8", "gb2312", $s0);
  $asc = ord($s{0}) * 256 + ord($s{1}) - 65536;
  if($asc>=-20319 and $asc<=-20284) return "A";
  if($asc>=-20283 and $asc<=-19776) return "B";
  if($asc>=-19775 and $asc<=-19219) return "C";
  if($asc>=-19218 and $asc<=-18711) return "D";
  if($asc>=-18710 and $asc<=-18527) return "E";
  if($asc>=-18526 and $asc<=-18240) return "F";
  if($asc>=-18239 and $asc<=-17923) return "G";
  if($asc>=-17922 and $asc<=-17418) return "H";
  if($asc>=-17417 and $asc<=-16475) return "J";
  if($asc>=-16474 and $asc<=-16213) return "K";
  if($asc>=-16212 and $asc<=-15641) return "L";
  if($asc>=-15640 and $asc<=-15166) return "M";
  if($asc>=-15165 and $asc<=-14923) return "N";
  if($asc>=-14922 and $asc<=-14915) return "O";
  if($asc>=-14914 and $asc<=-14631) return "P";
  if($asc>=-14630 and $asc<=-14150) return "Q";
  if($asc>=-14149 and $asc<=-14091) return "R";
  if($asc>=-14090 and $asc<=-13319) return "S";
  if($asc>=-13318 and $asc<=-12839) return "T";
  if($asc>=-12838 and $asc<=-12557) return "W";
  if($asc>=-12556 and $asc<=-11848) return "X";
  if($asc>=-11847 and $asc<=-11056) return "Y";
  if($asc>=-11055 and $asc<=-10247) return "Z";
  return '#';
}

/**
 * 用于过滤标签，输出没有html的干净的文本
 * @param string text 文本内容
 * @return string 处理后内容
 */
function custom_filter($text){
  $text = nl2br($text);
  $text = real_strip_tags($text);
  $text = str_ireplace(array("\r","\n","\t","&nbsp;"),'',$text);
  $text = htmlspecialchars($text,ENT_QUOTES);
  $text = trim($text);
  return $text;
}
function real_strip_tags($str, $allowable_tags="") {
  $str = stripslashes(htmlspecialchars_decode($str));
  return strip_tags($str, $allowable_tags);
}

/**
 * 获取指定日期对应星座
 *
 * @param integer $month 月份 1-12
 * @param integer $day 日期 1-31
 * @return boolean|string
 */
function getConstellation($month, $day)
{
  $day   = intval($day);
  $month = intval($month);
  if ($month < 1 || $month > 12 || $day < 1 || $day > 31) return false;
  $signs = array(
      array('20'=>'宝瓶座'),
      array('19'=>'双鱼座'),
      array('21'=>'白羊座'),
      array('20'=>'金牛座'),
      array('21'=>'双子座'),
      array('22'=>'巨蟹座'),
      array('23'=>'狮子座'),
      array('23'=>'处女座'),
      array('23'=>'天秤座'),
      array('24'=>'天蝎座'),
      array('22'=>'射手座'),
      array('22'=>'摩羯座')
  );
  list($start, $name) = each($signs[$month-1]);
  if ($day < $start)
    list($start, $name) = each($signs[($month-2 < 0) ? 11 : $month-2]);
  return $name;
}

/**
 * 字节格式化 把字节数格式为 B K M G T 描述的大小
 * @return string
 */
function byte_format($size, $dec=2) {
  $a = array("B", "KB", "MB", "GB", "TB", "PB");
  $pos = 0;
  while ($size >= 1024) {
    $size /= 1024;
    $pos++;
  }
  return round($size,$dec)." ".$a[$pos];
}


?>
