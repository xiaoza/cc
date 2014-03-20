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
?>
