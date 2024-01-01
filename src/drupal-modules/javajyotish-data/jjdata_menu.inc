<?php

/**
* Implements hook_menu()
*
* Displays various ebook data items via callbacks
*/
function javajyotish_data_menu()
{
  $items['view_chart_data'] = array(
	'title' => 'View chart data',
	'page callback' => 'view_chart_data',
	'page arguments' => array(1),
	'access callback' => 'jjdata_user_access',
	'access arguments' => array('view chart data', 1),
	'type' => MENU_NORMAL_ITEM,
  );
  /******
  $items['confirm_subscription'] = array(
	'title' => 'Subscription confirmation',
	'page callback' => 'subscription_confirmation',
	'page arguments' => array(1,2),
	'access callback' => 'jjdata_all_access',
	'access arguments' => array('view content', 1),
	'type' => MENU_CALLBACK,
  );
  *********/
  return $items;
}

// Deliberately left open

