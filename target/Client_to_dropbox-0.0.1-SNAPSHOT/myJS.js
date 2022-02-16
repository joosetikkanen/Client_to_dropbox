"use strict";

window.onload = checkConnection;

function checkConnection(){
	
	const q_params = new URLSearchParams(window.location.search)
	const code = q_params.get("code");
	if (code != null){
		document.getElementById("connect").disabled = true;
		document.getElementById("getToken").disabled = false;
	}
}


function connect(){
	var q_str = 'method=connect'
	doAjax("app", q_str, "connect_back", "post", 0);
}

function connect_back(result){
	window.location = result;
}

var token = "";
var account_id = "";
var imgDBPath = ""

var saveToken = (acc_token, acc_id) => {
	token = acc_token;
	account_id = acc_id;
}

var saveImageDBPath = (path) => {
	imgDBPath = path;
}

function getToken(){
	
	const q_params = new URLSearchParams(window.location.search)
	const code = q_params.get("code");
	var q_str = 'method=getToken&code=' + code;
	doAjax("app", q_str, "getToken_back", "post", 0);
}

function getToken_back(result){

	let div = document.getElementById("tokenInfo");
	let div2 = document.createElement("div")
	div2.setAttribute("id", "tokenData");
	div.parentNode.insertBefore(div2, div.nextSibling);
	let data = JSON.parse(result);
	for(var k in data){
		let datakey = document.createElement("span")
		datakey.setAttribute("style","font-style:italic;text-align:right")
		datakey.textContent = k + ":      "
		let datavalue = document.createElement("span")
		datavalue.textContent = data[k];

		div2.appendChild(datakey);
		div2.appendChild(datavalue);
		div2.appendChild(document.createElement("br"))
	}
	saveToken(data.access_token, data.account_id);
	document.getElementById("getToken").disabled = true;
	document.getElementById("getAccInfo").disabled = false;
	
}

function getAccountInfo(){
	
	var q_str = 'method=getAccInfo&token=' + token + "&acc_id=" + account_id;
	doAjax("app", q_str, "getAccountInfo_back", "post", 0);
}

function getAccountInfo_back(result){
	
	document.getElementById("tokenData").textContent = "";
	let div = document.getElementById("accInfo");
	let div2 = document.createElement("div")
	div2.setAttribute("id", "accData");
	div.parentNode.insertBefore(div2, div.nextSibling);
	let data = JSON.parse(result);
	printValues(data, div2);
	document.getElementById("getAccInfo").disabled = true;
	document.getElementById("upload").disabled = false;
}

let printValues = (data, div) => {
	
	for(var k in data){
		
		if (data[k] instanceof Object){
			printValues(data[k], div);
		}
		else{

			let datakey = document.createElement("span")
			datakey.setAttribute("style","font-style:italic;text-align:right")
			datakey.textContent = k + ":      "
			let datavalue = document.createElement("span")
			datavalue.textContent = data[k];
			
			div.appendChild(datakey);
			div.appendChild(datavalue);
			div.appendChild(document.createElement("br"))
		}
	}
}


function upload(){
	
	let filepath = ".\\images\\Ve2HCLv.png"
	var q_str = 'method=upload&token=' + token + "&pathToFile=" + filepath;
	doAjax("app", q_str, "upload_back", "post", 0);
}

function upload_back(result){
	
	let data = JSON.parse(result);
	let div = document.getElementById("uploadDiv");
	let uploadText = document.createElement("span");
	uploadText.setAttribute("id", "uploadText");
	let folder = data.path_display.substring(1, data.path_display.lastIndexOf("/")+1);
	uploadText.innerHTML = "Upload image to <span style=\"font-style:italic\">" + folder + "</span> folder under the name <span style=\"font-style:italic\">" + data["name"] + "</span>";
	div.appendChild(uploadText);
	let accDiv = document.getElementById("accData");
	accDiv.textContent = "";
	let img = document.createElement("img");
	img.setAttribute("src", ".\\images\\Ve2HCLv.png");
	accDiv.appendChild(img);
	saveImageDBPath(data.path_display);
	document.getElementById("upload").disabled = true;
	document.getElementById("delete").disabled = false;
	document.getElementById("deleteText").remove()

}

function deleteFile(){
	
	var q_str = 'method=delete&token=' + token + "&pathToDBFile=" + imgDBPath;
	doAjax("app", q_str, "delete_back", "post", 0);
}

function delete_back(result){
	
	let data = JSON.parse(result);
	let div = document.getElementById("deleteDiv");
	let deleteText = document.createElement("span");
	deleteText.setAttribute("id", "deleteText");
	let folder = data.metadata.path_display.substring(1, data.metadata.path_display.lastIndexOf("/")+1);
	deleteText.innerHTML = "Deleted image <span style=\"font-style:italic\">" + data.metadata.name + "</span> from folder <span style=\"font-style:italic\">" + folder + "</span>";
	div.appendChild(deleteText);
	let accDiv = document.getElementById("accData");
	accDiv.textContent = "";
	document.getElementById("uploadText").remove();
	document.getElementById("delete").disabled = true;
	document.getElementById("upload").disabled = false;
}


