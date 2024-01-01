<?php

// Helper function to change a form element if no default exists
function jjdata_conditional_element_change( &$form, $element_id, $newvalue )
{
  //printf( "<!-- %s=%s: %s -->\n", $element_id, $newvalue, print_r($form[$element_id], TRUE) );
  // Drupal 7 buries values for node entry of field_sql_storage very deep, as $form[$element_id]['und'][0]['value']['#default_value'], etc.
  // Regular node attributes like title are more normal. Note that we can't use isset for a value that is probably
  // defined as an array element but whose value may not be set
  if (array_key_exists( '#default_value', $form[$element_id] ))
  {
	// Normal node attribute
	//printf( "<!-- normal node element [%s] setting to %s: %s -->\n", $element_id, $newvalue, print_r($form[$element_id], TRUE) );
	if ($form[$element_id]['#default_value'] == "") $form[$element_id]['#default_value'] = $newvalue;
  }
  else
  {
	//printf( "<!-- %s=%s: %s -->\n", $element_id, $newvalue, print_r($form[$element_id], TRUE) );
	// field_sql_storage addon
	if (!isset( $form[$element_id]['und'][0]['value']['#default_value'] ) || $form[$element_id]['und'][0]['value']['#default_value'] == "")
	$form[$element_id]['und'][0]['value']['#default_value'] = $newvalue;
  }
}

/****
 * Implements hook_form_alter
*****/
function javajyotish_data_form_alter( &$form, &$form_state, $form_id )
{
	// For node create book review, check for default values
	if ($form_id == "book_review_node_form" && isset( $_REQUEST['asin'] ))
	{
		$msg = "";
		//$msg .= "form_id={$form_id}";
		$msg .= " asin={$_REQUEST['asin']}";
		//printf( "<!-- %s -->", print_r($form,TRUE) );
		if (isset( $_REQUEST['title'] )) conditional_element_change( $form, "title", $_REQUEST['title'] );
		/* if (isset( $_REQUEST['asin'] )) */ conditional_element_change( $form, "field_isbn", $_REQUEST['asin'] );
		if (isset( $_REQUEST['author'] )) conditional_element_change( $form, "field_author", $_REQUEST['author'] );
		// Strip out GET variables in submit
		if (isset( $form['#action'] ))
		{
			//  printf( "<!-- action=%s -->\n", $form['#action'] );
			if (strstr($form['#action'], "/node/add/book-review?"))
			{
				//printf( "<!-- Discarded original action: %s -->\n", $form['#action'] );
				$form['#action'] = "/node/add/book-review";
			}
		}
		//else printf( "<!-- ENTIRE FORM: %s -->\n", print_r($form, TRUE) );
		//printf( "<!-- %s -->", $msg );
		//watchdog( "ebook_data_form_alter", $msg );
	}
}

// Subscribe form
function javajyotish_data_request( &$form_state )
{
  $form['#redirect'] = "confirmation_sent";
  $form['description'] = array(
	'#type' => 'item',
	'#title' => t('Request subscription'),
  );

  $form['name'] = array(
	'#type' => 'textfield',
	'#title' => t('Name (optional)'),
	'#description' => 'Your name',
	'#size' => 30,
	'#maxlength' => 128,
   );
   $form['email'] = array(
	'#type' => 'textfield',
	'#title' => t('Email address'),
	'#description' => 'Your email address',
	'#required' => TRUE,
	'#size' => 50,
	'#maxlength' => 250,
   );
   $form['submit'] = array(
	'#type' => 'submit',
	'#value' => 'Subscribe',
   );
   return $form;
}

// Validation for email
function javajyotish_data_request_validate( $form, &$form_state )
{
  $email = $form_state['values']['email'];
  if (!preg_match( '/^([^@]+)@(.+\..+)$/', $email, $a ))
  {
	form_set_error( 'email', t('Invalid email address'));
  }
  // This would get displayed, but not from _submit...
  //else drupal_set_message( 'Email passed validation' );
}

// Submit handler
function form_javajyotish_data_request_submit( $form, &$form_state )
{
  // name is optional, email required
  // Check for existing request
  db_query( "INSERT INTO sasub_subscriber (name, email, when_requested, request_ip) VALUES ('%s','%s',NOW(),'%s')", $form_state['values']['name'], $form_state['values']['email'], $_SERVER['REMOTE_ADDR'] );
  // Get sid
  $new_sid = db_last_insert_id( 'sasub_subscriber', 'sid' );
  if ($new_sid > 0)
  {
    global $base_url;
    $hash = md5("{$new_sid}:" . date('YmdHis'));
    db_query( "INSERT INTO sasub_validation (sid, when_issued, hash) VALUES ('%d', NOW(), '%s')", $new_sid, $hash );
    // Construct message and send email using mime mail
    $site_url = $base_url;
    $subscription_name = "content on our site";
    $confirm_url = l("{$site_url}/confirm_subscription/{$hash}", "confirm_subscription/{$hash}", array('html' => TRUE));
    $cancel_url = l("{$site_url}/confirm_subscription/{$hash}/cancel", "confirm_subscription/{$hash}/cancel", array('html' => TRUE));
    $confirm_sender = "site admin";
    $content = "<p>Someone, probably you, visited {$site_url} and requested a subscription to {$subscription_name}.<br> Before we begin sending you anything, we need you to confirm by clicking on this link:<br>{$confirm_url}</p>";
    $content .= "<p>If at any time you wish to cancel your subscription, use this link:<br>{$cancel_url}<br>(You may wish to save this email)</p>";
    $message = array( 'type' => 'mail', 'subject' => 'Please confirm your subscription request', 'body' => array(
	'header' => '<h1>Confirmation needed</h1>',
	'content' => $content,
	'footer' => "<p>This message sent by {$confirm_sender}</p>"
	) );
    messaging_message_send($form_state['values']['email'], $message, 'mimemail');
  }
  //drupal_set_message( t('Subscription confirmation for @name has been sent to @email. Check your inbox and follow instructions to complete  your subscription request.'), array('@name' => $form_state['values']['name'], '@email' => $form_state['values']['email']) );
  // Message never gets displayed
  //drupal_set_message( 'Subscription confirmation has been sent to ' + $form_state['values']['email'] );
}

/*********
// Page handler for subscription request
function request_subscription( $uid )
{
  $content = drupal_get_form('form_javajyotish_data_request');
  return $content;
}

// Page handler for subscription confirmation sent
function subscription_confirmation_sent()
{
  $content = "<p>A confirmation request has been emailed to you. Please check your email for further instructions.</p>";
  return $content;
}
*************/

/******************* Add subscription ********************/

/**************
// Subscribe form
function form_add_subscription( &$form_state )
{
  $form['#redirect'] = "view_subscriptions";
  $form['description'] = array(
	'#type' => 'item',
	'#title' => t('Add subscription type'),
  );

  $form['subname'] = array(
	'#type' => 'textfield',
	'#title' => t('Name'),
	'#description' => 'Name of subscription, e.g. News',
	'#required' => TRUE,
	'#size' => 30,
	'#maxlength' => 80,
   );
   $form['ctype'] = array(
	'#type' => 'textfield',
	'#title' => t('Content type'),
	'#description' => 'Content type to include, e.g. image',
	'#size' => 30,
	'#maxlength' => 32,
   );
   $form['xctype'] = array(
	'#type' => 'textfield',
	'#title' => t('Exclude type'),
	'#description' => 'Content type to exclude, e.g. image',
	'#size' => 30,
	'#maxlength' => 32,
   );
   $form['footer'] = array(
	'#type' => 'textarea',
	'#rows' => 4,
	'#cols' => 60,
	'#description' => 'Footer HTML to add to emails',
   );
   $form['submit'] = array(
	'#type' => 'submit',
	'#value' => 'Add subscription',
   );
   return $form;
}
*************/

/**********
// Validation for email
function form_add_subscription_validate( $form, &$form_state )
{
  $email = $form_state['values']['email'];
  if (!preg_match( '/^([^@]+)@(.+\..+)$/', $email, $a ))
  {
	form_set_error( 'email', t('Invalid email address'));
  }
  // This would get displayed, but not from _submit...
  //else drupal_set_message( 'Email passed validation' );
}
*********/

/***************
// Submit handler
function form_add_subscription_submit( $form, &$form_state )
{
  // FIXME Check for existing request
  db_query( "INSERT INTO sasub_subscription (subname, ctype, xctype, footer) VALUES ('%s','%s','%s','%s')",
	$form_state['values']['subname'],
	$form_state['values']['ctype'],
	$form_state['values']['xctype'],
	$form_state['values']['footer'] );
  // Get subid
  $new_subid = db_last_insert_id( 'sasub_subscription', 'subid' );
  if ($new_sid > 0)
  {
    drupal_set_message( "Added subscription {$new_subid}" );
  }
  //drupal_set_message( t('Subscription confirmation for @name has been sent to @email. Check your inbox and follow instructions to complete  your subscription request.'), array('@name' => $form_state['values']['name'], '@email' => $form_state['values']['email']) );
  // Message never gets displayed
  //drupal_set_message( 'Subscription confirmation has been sent to ' + $form_state['values']['email'] );
}

// Add a new subscription type
function add_subscription()
{
  return drupal_get_form('form_add_subscription');
}
**************/

/**** removed
// Page handler for messaging test
function subscription_messaging_test()
{
  // Yes, we get 'mimemail' => 'Mime Mail'
  //return '<p>Method list:<br/>' . print_r( messaging_method_list(), TRUE ) . '</p>';
    // Get contents of node
   $testnode = node_load(2);
    // Build components
   $nodebuild = node_build_content($testnode);
   $header = "<h1>Message header v{$ver}</h1>";
   // We want $nodebuild->title, $nodebuild->body and $nodebuild->images[0][preview] if $nodebuild->type == "image"
   //$content = "<p>Message contents ver {$ver}</p><p>Another paragraph with <a href=\"http://gothmog\">hyperlink</a></p>";
   //$content = "<!-- raw view -->" . node_view($testnode) . "<!-- built -->" . print_r( $nodebuild, TRUE );
   $header = "<h1>{$nodebuild->title}</h1>";
   $content = $nodebuild->body;
   if ($nodebuild->type == "image")
   {
	$content .= sprintf( "<br><img src=\"http://gothmog/drup/%s\"/>", $nodebuild->images['preview'] );
	// Possibly others?
   }
   else $content .= "<br>Not image, type=[{$nodebuild->type}]";
    // Construct message and send email using mime mail
   $ver = "16";
    $message = array( 'type' => 'mail', 'subject' => "Test mime msg ver {$ver}", 'body' => array(
	'header' => $header,
	'content' => $content,
	'footer' => '<p style="font-size:8pt;">View original: </p>'
	) );
   watchdog( 'sasub', 'Starting attempt ver @ver', array('@ver' => $ver) );
   // Not sure what kind of params are needed for messaging_mime_mail_send_msg
   // Use drupal mail
   if (messaging_message_send('henry.groover@gmail.com', $message, 'mimemail'))
   {
	return "<P>Sent successfully v{$ver}</p>";
   }
   else
   {
	return "<p>Send failed v{$ver}</p>";
   }
}****/

// Deliberately left open
