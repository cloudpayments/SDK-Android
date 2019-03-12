<?php
	include 'cp_config.php';

	charge();

	function charge() {

		$postdata = file_get_contents('php://input');
		$json_obj = json_decode($postdata, true);

		$amount = $json_obj['amount'];
		$currency = $json_obj['currency'];
		$name = $json_obj['name'];
		$ipAddress = '192.168.0.1';
		$cardCryptogramPacket = $json_obj['card_cryptogram_packet'];
		$description = $json_obj['description'];
		$accountId = $json_obj['account_id'];
		$jsonData = $json_obj['json_data'];

		$args=array(
                    'Amount'=>$amount,
             		'Currency'=>$currency,
             		'Name'=>$name,
             		'IpAddress'=>$ipAddress,
             		'CardCryptogramPacket'=>$cardCryptogramPacket,
             		'Description'=>$description,
             		'AccountId'=>$accountId,
             		'JsonData'=>$jsonData
                );

		$ch = curl_init(); 
		curl_setopt($ch, CURLOPT_URL, API_URL . 'charge'); 
		curl_setopt($ch, CURLOPT_HTTPAUTH, CURLAUTH_BASIC);
        curl_setopt($ch, CURLOPT_USERPWD, MERCHANT_PUBLIC_ID . ":" . MERCHANT_API_PASS);
		curl_setopt($ch, CURLOPT_POST, true); 
		curl_setopt($ch, CURLOPT_POSTFIELDS, $args); 
		curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, API_TIMEOUT);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, true); 
		
		$result = curl_exec($ch); 
		echo $result;
		curl_close($ch); 
	}
?>