<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<c:set var="data"
	value="${requestScope['fr.humanum.masa.user.UserData']}" />

<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="resources/bootstrap/css/bootstrap.css">
<link rel="stylesheet" href="resources/css/style.css">
<link rel="stylesheet" href="resources/css/jquery-ui.min.css">
<script src="resources/js/jquery-1.11.3.js"></script>
<script src="resources/bootstrap/js/bootstrap.js"></script>
<script src="resources/js/jquery-ui.min.js"></script>
<title>Utilisateurs</title>
<script type="text/javascript">

$( function() {
    var dialog1,dialog2,dialog3, form1, form2,form3;
    
  
    dialog1 = $( "#dialog-form" ).dialog({
        autoOpen: false,
        height: 600,
        width: 600,
        modal: true,
        buttons: {
      	  "Sauvegarder": function() {
          		document.forms.userForm.submit();
            },
          Cancel: function() {
            dialog1.dialog( "close" );
          }
        },
        close: function() {
          form1[ 0 ].reset();
          
        }
      });
    
    dialog3 = $( "#dialog-form-mdp" ).dialog({
        autoOpen: false,
        height: 500,
        width: 500,
        modal: true,
        buttons: {
      	  "Sauvegarder": function() {
      			var mdp1=document.forms.userFormMdp.password.value;
        	    var mdp2=document.forms.userFormMdp.verification.value;
    	      	if((mdp2===mdp1)){
    	      	 document.forms.userFormMdp.submit();
    	      	}
          		
            },
          Cancel: function() {
            dialog3.dialog( "close" );
          }
        },
        close: function() {
          form3[ 0 ].reset();
          
        }
      });
      
      dialog2 = $( "#dialog-form-edit" ).dialog({
          autoOpen: false,
          height: 600,
          width: 600,
          modal: true,
          buttons: {
        	  "Sauvegarder": function() {
        		document.forms.userFormEdit.submit();
                },
            Cancel: function() {
              dialog2.dialog( "close" );
            }
          },
          close: function() {
            form2[ 0 ].reset();
            
          }
        });
    
      form1= dialog1.find( "form" ).on( "submit", function( event ) {
        event.preventDefault();
        document.forms.userForm.submit();
      });
      
      form2= dialog2.find( "form" ).on( "submit", function( event ) {
          event.preventDefault();
          document.forms.userFormEdit.submit();
        });
      
      form3= dialog3.find( "form" ).on( "submit", function( event ) {
          event.preventDefault();
          
        });
      
      $( ".edit" ).on( "click", function() {
          dialog2.dialog( "open" );
        });
      
      $( "#add" ).button().on( "click", function() {
        dialog1.dialog( "open" );
      });
      
      $( ".mdp" ).on( "click", function() {
          dialog3.dialog( "open" );
        });
    } );
    
  //modification des données d'un utilisateur
    function edit(name,email,admin){
    	document.forms.userFormEdit.name.value=name;
    	document.forms.userFormEdit.email.value=email;
    	
    	if(admin==="ROLE_ADMIN"){
    		document.forms.userFormEdit.admin.checked=true;
    	}else{
    		document.forms.userFormEdit.admin.checked=false;
    	}
    	
    }
  	
  	function editPassword(email){
  		document.forms.userFormMdp.email.value=email;
  	}
  
  
</script>
<style type="text/css">
	.msg {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
	color: #31708f;
	background-color: #d9edf7;
	border-color: #bce8f1;
	width: 90%;
	margin: auto;
	margin-top: 50px;
}
</style>
</head>
<body>
	<jsp:include page="header.jsp">
		<jsp:param name="active" value="admin"/>
	</jsp:include>
	<br><br><br>
	<div class="container">
		<h5>Administration</h5>
		
		<c:if test="${data.msg!=null}">
			<div class="msg" style="text-align: center;">
			  <strong>${data.msg}</strong>
			</div>
		</c:if>
		<br>
		
		
	</div>
</body>
<script type="text/javascript">
$( document ).ready(function() {
	
});
</script>
</html>