<?php
/**
 * ��ȡ�ַ����ĳ���
  * ����ʱ, ���ֻ�ȫ���ַ�ռ1������, Ӣ���ַ�ռ0.5������
   * @param string  $str
    * @param boolean $filter �Ƿ����html��ǩ
     * @return int �ַ����ĳ���
      */
      function get_str_length($str, $filter = false){
        if ($filter) {
                $str = html_entity_decode($str, ENT_QUOTES);
                        $str = strip_tags($str);
                            }
                                return (strlen($str) + mb_strlen($str, 'UTF8')) / 4;
                                }
?>
