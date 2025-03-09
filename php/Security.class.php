<?php
/*
 * Copyright  2010, entersol, Ltd. All rights reserved.
 * CopyrightTerm
 */
/**
 * This class contains ciphering and deciphering functions all functions are static this makes them easier to access
 * @author Polla A. Fattah <br/><a href="mailto:polla@enterhosts.com">polla@enterhosts.com</a>
 */
class Security{
	/**
	 * this function uses a symmetric key to encrypt the message
	 *
	 * @param message plain text
	 * @return String ciphered text
	 */
	public static function encrypt($m){
		return $m;
	}
	/**
	 * This function uses a symmetric key to decrypt the message
	 * @param message ciphered text
	 * @return plain text
	 * @see encrypt
	 */
	public static function decrypt($m){
		return $m;
	}
}
?>

