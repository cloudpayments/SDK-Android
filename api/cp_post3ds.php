<?php
	include 'cp_config.php';

	post3ds();

	function post3ds() {

		$postdata = file_get_contents('php://input');
		$json_obj = json_decode($postdata, true);

		$transactionId = $json_obj['transaction_id'];
		$paRes = $json_obj['pa_res'];

		$args=array(
                    'TransactionId'=>$transactionId,
             		'PaRes'=>$paRes
                );

		$ch = curl_init(); 
		curl_setopt($ch, CURLOPT_URL, API_URL . 'post3ds'); 
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
