/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */

Array.prototype.exists = function(search) {
	for ( var i = 0; i < this.length; i++) {
		if (this[i] == search) {
			return true;
		}
	}

	return false;
}

function isValueOnArray(value, v) {
	for (var i=0 ; i<v.length ; i++) {
		if (v[i][1] == value) {
			return true;
		}
	}
	return false;	
}

function isValidClass(value) {
	return isValueOnArray(value, dataClasses);
}

function isValidRelation(value) {
	return isValueOnArray(value, dataObjectProperties);
}

function getFullTermDefinition(value) {
	for (var i=0 ; i<dataFull.length ; i++) {
		if (dataFull[i][1] == value) {
			return dataFull[i][0];
		}
	}
	return value;
}

function getAutocompleteStore() {
	return new Ext.data.Store( {
		url : 'result.xml',
		reader : new Ext.data.XmlReader( {
			record : 'rs',
			totalProperty : 'totalCount',
			id : 'id'
		}, [ 'id', 'value' ])
	});
}

function getTermAutocompleteComboBox(label, rightField, filterComboBox, pctext) { // @@@ Search for
	var store = new Ext.data.ArrayStore( {
		fields : [ 'id', 'value', 'group' ],
		data : dataFull
	});
	
	return new Ext.ux.form.GroupingComboBox( {
		emptyText: pctext, //added by @gs
    store : store,
    anchor: '90%', 
		groupField: 'group',
		fieldLabel : label,
		displayField : 'value',
		typeAhead : true,
		triggerAction : 'all',
    // allowBlank: 'true', // it is overwritten - it is set to 'true' when getTermAutocompleteComboBox is called so this configuration
    mode : 'local',
		queryParam : 'query',
		selectOnFocus : true,
    //width: 110,
		// autoWidth : true,
		hideTrigger : true,
		listeners: {
			'change': function(field, newValue, oldValue) {		    			
				if (isValidRelation(newValue)) {
					filterComboBox.enable();
//					operationComboBox.enable();
					rightField.enable();
					filterComboBox.setValue('some');
//					operationComboBox.setValue('of type');
				}
			    else {
					filterComboBox.disable();
					filterComboBox.clearValue();
//					operationComboBox.disable();
//					operationComboBox.clearValue();
					rightField.disable();
					rightField.clearValue();
				}
			}
		},
		validator: function(value) {		    	
			if (!value || value=='' || isValidClass(value)) {
				return true;
			}
			else if (isValidRelation(value)) {
				return true;
			}
		  else {
				return 'The term value needs to be a class or a relation';
			}
		} 
	});
}

function getRightAutocompleteComboBox(label) {      // @@@ expression
	var store = new Ext.data.ArrayStore( {
		fields : [ 'id', 'value' ],
		data : dataClasses
	});

	return new Ext.form.ComboBox( {
    emptyText: 'entity or value and/or expression in parenthesis',   //added by @gs - works as placeholder
    store : store,
    anchor: '90%', // added by @gs - sets relative width
		fieldLabel : label,
		displayField : 'value',
		typeAhead : true,
		triggerAction : 'all',
		mode : 'local',
		queryParam : 'query',
		selectOnFocus : true, 
    //autoWidth: true,
    hideTrigger : true,
    validator: function(value) {
       return true;
		}
	});
}

function getStaticComboBox(label, /*width,*/ data, pctext) { //pctext added by @gs
	var store = new Ext.data.ArrayStore( {
		fields : [ 'id', 'value' ],
		data : data
	});

	return new Ext.form.ComboBox( {
    emptyText: pctext, //added by @gs
    store : store,
    anchor: '90%', // added by @gs - sets relative width
		fieldLabel : label,
		displayField : 'value',
		valueField: 'id',
		typeAhead : false,
		editable: false,
		forceSelection: true,
		autoSelect: true,
		mode : 'local',
		//width: width,
		forceSelection : true,
		triggerAction : 'all',
		selectOnFocus : true
	});
}

function getConditionalRadioButtons() {
	return new Ext.form.RadioGroup({
		fieldLabel : 'Condition',
		autoHeight : true,
    //autoWidth : true,  
	  width : 110,
		items : [ {
			boxLabel : 'and',
			name : 'condition'
		}, {
			boxLabel : 'or',
			name : 'condition'
		} ]
	});
}

var myMS_query;
var mySP_query;
var msStore;
var spStore;

function appInit(expression, reasonerValue, ontologyValue) {

  var store = getAutocompleteStore();
	
	var filterComboBox = getStaticComboBox("Restriction", /*110,*/ [ [ 'some', 'some' ], [ 'min', 'min' ], [ 'max', 'max' ], [ 'only', 'only' ],
		[ 'value', 'value' ], [ 'exactly', 'exactly' ]], "select one...");  //last parameter adde by @gs
  filterComboBox.disable();

	/*
	var operationComboBox = getStaticComboBox("with value", 100, [ 
		[ 'of type', 'of type'], [ '<', '<' ], [ '<=', '<=' ], [ '>', '>' ], [ '>=', '>=' ],
		[ 'length', 'length' ], [ 'maxLength', 'maxLength' ], [ 'minLength', 'minLength' ],
		[ 'pattern', 'pattern' ], [ 'totalDigits', 'totalDigits' ], [ 'fractionDigits', 'fractionDigits' ]]
	);
	operationComboBox.disable();
	*/
	
	var rightField = getRightAutocompleteComboBox("Expression"); // @@ Expression
	rightField.disable();
	
	var conditionalRadioButtons = getConditionalRadioButtons();   // @@@ Condition
	
	if (! expression) {
        conditionalRadioButtons.disable();
    }
	else {
		conditionalRadioButtons.enable();
	}
	
  // @@ Search for
	var termField = getTermAutocompleteComboBox("Search for", rightField, filterComboBox, "entity or kind of relation");
	termField.allowBlank = true; // orignally was 'false'

	var notCheckbox = new Ext.form.Checkbox({
    width: '50',
    boxLabel : 'not',
		name : 'condition'
	});
	

	var form = new Ext.FormPanel( {
		labelWidth : 70, 
		autoHeight : true,
    monitorValid : true,
		bodyStyle : 'padding:5px',
   // bodyStyle : 'font:12px tahoma,arial,helvetica,sans-serif; padding:5px; text-align:right;',		
    bodyCfg: {
        cls: 'need-border-class .x-panel-body',  // Default class not applied if Custom element specified 
        style: {'border':'1px solid #C0C0C0;'},
    },
    items : [ /*{
			xtype: 'compositefield',
      //fieldLabel : ' ',   
			items: [ notCheckbox, termField]      
		},*/ notCheckbox, termField, filterComboBox, rightField,  /*
    {
			xtype: 'compositefield',
			fieldLabel : 'Search for',   
			items: [ termField ]     
		}, 
    filterComboBox,    // @@@ Restriction
    {
			xtype: 'compositefield',
			fieldLabel : 'Expression',        
			items: [ rightField ]   
		}, */
    conditionalRadioButtons],
		buttonAlign: 'center',
    buttons : [ {
			xtype : 'button',
			text : 'Add term',
			handler: function (event, button) {
				if (form.getForm().isValid()) {
					var selectedRadio = conditionalRadioButtons.getValue();
					//if (queryLabel.getValue().length > 0 && !selectedRadio) {
					if (mseditor.getValue().length > 0 && !selectedRadio) {
            Ext.MessageBox.alert('Error', 'Either a "Condition" must be selected or press the "Clear query" button to start with a new request');
						return;
					}

					if (isValidRelation(termField.getValue())) {
						if (!filterComboBox.getValue() || !rightField.getValue()) {
							Ext.MessageBox.alert('Error', 'A "Restriction" and an "Expression" must be selected for relations');
							return;
						}
						
						/* 
						if (operationComboBox.getValue() && !filterComboBox.getValue()) {
							Ext.MessageBox.alert('Error', 'A filter condition must be selected when you use an operation');
							return;
						}
						*/
					}

					var value = "";
					
					if (selectedRadio) {
						value += " " + selectedRadio.boxLabel + " ";
					}

					if (notCheckbox.getValue()) {
						value += "not ";
					}
					
					value += getFullTermDefinition(termField.getValue()) + " ";
						
					if (isValidRelation(termField.getValue())) {
						value += filterComboBox.getValue() + " ";
						
						/*  
						if (operationComboBox.getValue() && operationComboBox.getValue() != 'of type') {
							value += operationComboBox.getValue() + " ";
						}
						*/
						
						value += getFullTermDefinition(rightField.getValue());
					}

				// queryLabel.setValue(queryLabel.getValue() + value);      
			    mseditor.setValue(mseditor.getValue() + value);        //handler if codemirror is used - added by @gs
          
          conditionalRadioButtons.enable();
					form.getForm().reset();
				}
				else {
					 Ext.MessageBox.alert('Error', 'Can not add term until the information are valid');	
				}
			}
		} ]
	});

	var reasonerCombobox = getStaticComboBox("Reasoner", /*200,*/ [[ 'FACTPLUSPLUS', 'Fact++'], [ 'PELLET', 'Pellet' ], [ 'HERMIT', 'HermiT' ]]);
	
  reasonerCombobox.setValue(reasonerValue);
	
  Ext.state.Manager.setProvider(new Ext.state.CookieProvider());
	Ext.state.Manager.getProvider(); 
    
    var urlStore = new Ext.data.SimpleStore({ 
        fields: ['url'], 
        data: Ext.state.Manager.get('URLStore', []) 
    });    
    var path = window.location.pathname;
    var defaultOnt = "http://"+window.location.host+path.substring(0, path.lastIndexOf('/'))+'/dspace-ont';
    var aRecord = new urlStore.recordType({url:defaultOnt});
    urlStore.insert(0, aRecord);
    
    function saveURL(url) { 
        if (Ext.isEmpty(url)) 
            return;
        
        urlStore.clearFilter(false);

        if (urlStore.find('url', url) < 0) { 
            var data = [[url],'']; 
            var count = urlStore.getTotalCount(); 
            var limit = count > 10? 9: count; 
             
            for (var i = 1; i <= limit; i++) 
                data.push([urlStore.getAt(i).get('url')]);
            
            if (Ext.state.Manager.getProvider()) 
                Ext.state.Manager.set('URLStore', data); 
             
            urlStore.loadData(data); 
        } 
    }	
   
	var ontologyCombobox = new Ext.form.ComboBox({
		   fieldLabel: 'Ontology',
		   displayField: 'url',
		   valueField: 'url',
       anchor : '90%',      //works, even though layout of optionsForm is not set to "anchor"....
		   vtype: 'url',
		   vtypeText: 'Please enter a valid URL for your ontology',
		   store: urlStore,
		   triggerAction:'all',
		   typeAhead:true,
		   mode:'local',
		   minChars:1,
		   //forceSelection:true,
		   hideTrigger:true,
		   value: ontologyValue,
		});
	ontologyCombobox.setValue(ontologyValue);
	
  
	var optionsForm = new Ext.FormPanel( {
		labelWidth : 70,
		monitorValid : true,
		items : [ reasonerCombobox, ontologyCombobox],
		bodyStyle : 'padding:5px',
    buttonAlign: 'center',
		buttons : [ {
			xtype : 'button',
			text : 'Save options',
			formBind: true,
			handler: function (event, button) {
				
				saveURL(ontologyCombobox.getValue());
			  window.location = 'semantic-search?URL=' + encodeURI(ontologyCombobox.getValue()).replace('+', '%2B') + '&reasoner=' + reasonerCombobox.getValue();
			}
		},{
			xtype : 'button',
			text : 'Reload',
			formBind: true,
			handler: function (event, button) {
				window.location = 'semantic-search?URL=' + encodeURI(ontologyCombobox.getValue()).replace('+', '%2B') + '&reasoner=' + reasonerCombobox.getValue() + '&reload=true';				
			}
		} ]
	});
 

////////////////////////  MANCHESTER SYNTAX /////////////////////////////////

   msStore = new Ext.data.SimpleStore({ 
        fields: ['query'],
        data: Ext.state.Manager.get('MSStore', []) 
  });

 /* function saveMSQuery() { 
      
      if (Ext.isEmpty(query)) 
         return;

      msStore.clearFilter(false);

      if (msStore.findExact('query', query) < 0) {  // replaced "find" with "findExact"

         var msdata = [[query, 'query']];   // σωστό!
         var count = msStore.getTotalCount(); 
         var limit = count >= 10? 9: count;  //@gs "count>=10"! and not "count>10"
         
          for (var i = 0; i < limit; i++) {
              msdata.push([msStore.getAt(i).get('query')]); 
          }
                                            
          if (Ext.state.Manager.getProvider()) 
             Ext.state.Manager.set('MSStore', msdata); 
          
           msStore.removeAll();
           msStore.loadData(msdata);     
        }
      }   */

    

var mshistoryGrid = new Ext.grid.GridPanel({
    id: 'mshistoryGrid',
    frame: false, 
    border: true,
    store: msStore,
    //bodyStyle: 'text-align:left; border-right-style:solid; border-width:1px;',
    viewConfig: {
      forceFit: true,       // for column to fit exactly the panel width
      scrollOffset: 0,
      headersDisabled: true //@gs - for not highlighting header on mouse over       
    },     
    columns: [
            {id:'mshistory', header: 'Query History', dataIndex:'query', sortable: false, resizable: false}   // query: same as spstore
    ],
    enableHdMenu: false,      // disables header menu
    height: 70, //  the height of its container
    hideHeaders: false,
    stripeRows: true,
});   
  
  var mshistoryPanel = new Ext.Panel({
    id: 'mshistoryPanel',
    floating: true,   // for not being like having transparency
    frame: false,
    boxMaxWidth : 500,    //750
    boxMinWidth : 150,
    //border: false,  // false because we added bodyStyle at grid
    bodyCfg: { //@gs DO NOT DELETE - places border to the history panel
        cls: 'need-border-class x-panel-body',  // Default class not applied if Custom element specified 
        style: {'border':'1px solid #C0C0C0;'},
    }, 
    layout:{
        type:'fit',
        align:'stretch'
    },
    height: 70,     //a bit less than queryPanel
    items: [mshistoryGrid],
    hidden: true,
    shadow: true,
    x:0,
    y:5
 }); 



var reasonerLabel = new Ext.form.Label({
    //text : "Loaded reasoner is " + reasonerValue,
    html : "Loaded reasoner is " + reasonerValue,
   });
  
var generatedQueryLabel = new Ext.form.Label({
    html : "Generated"+"</br>"+"query: ",
    //flex: 1,    //do not set!
    });  
  
  
var generatedQuery = new Ext.Panel( {
		id: 'generatedQuery',
    //fieldLabel: 'Generated query:',
    frame: false,
    border: false,
    height: 36,
    hidden: false,
    bodyCfg: { //@gs DO NOT DELETE - hides double border
        cls: 'need-border-class x-panel-body',  // Default class not applied if Custom element specified 
        style: {'border':'1px solid #C0C0C0;'},
    }, 
    items: [
    {
      style: 'text-align:left;',
      html: '<textarea id="msquery" name="ms" style="display:none;"></textarea>'
    }],
    x: 0,
    y: 0
  });


  var msWithHistory = new Ext.Panel({
    id: 'msWithHistory',
    frame: false,
    border: true,
    height: 70,
    flex: 9,
    layout: 'absolute',
    items: [generatedQuery, mshistoryPanel]
 });


  var msqueryPanel = new Ext.Panel({
    id: 'msqueryPanel',
    frame: false,
    border: true,
    height: 70,
    layout: {
      type: 'hbox',
      align: 'strech',
    },
    bodyCfg: {
        cls: 'no-border-class x-panel-body',  // Default class not applied if Custom element specified 
        style: {'border':'none;'},
    },  
    bodyStyle : 'text-align:left;', 
    items: [generatedQueryLabel, msWithHistory, {
      xtype: 'button',
      id: 'mshistoryButton',
      iconCls: 'add24',
      arrowAlign: 'bottom',
      height: 36, // for absolute layout
      width: 24,  // for absolute layout - do not change
      flex: 1,
      style: {              // always on top of panels
          'z-index': 1000 
      } ,
      menu:[], // fake menu, so as to show arrow on the button
      handler: mshistoryBtnHandler   // see below
    }],
 });

 var mslabelsAndFields = new Ext.Panel( {
		id: 'mslabelsAndFields',
    frame: false,
    border: false,
    hidden: false,
    autowidth: true,
    anchor: '95%',     //searchPanel's layout is set to 'anchor'
    //height: 100,   
    bodyStyle : 'padding:5px; text-align:right;',
    items: [ msqueryPanel, reasonerLabel],
  });

	var searchPanel = new Ext.Panel( {
    title : 'Search',
		buttonAlign : 'center',
		autoHeight : true,
    monitorValid : true,
    layout: 'anchor',   // necessary because controls msLabelsAndFields' width
    items : [ form,   mslabelsAndFields],      // instead of generatedquery
     //@gs - the following bodyCFG was added so as to remove the border that surrounds
    //the items of this Panel (excluding buttons) - this border appears becaues we used the renderTo option
  	bodyCfg: {
        cls: 'no-border-class x-panel-body',  // Default class not applied if Custom element specified 
        style: {'border-style':'none;'},
    },    
    buttons : [ {
			xtype : 'button',
			text : 'Search',
			handler: function(event) {
			  if (mseditor.getValue().length>0) {
					
          //var mscur = mseditor.getValue();
          mshistoryPanel.setVisible(false);
          generatedQuery.setVisible(true);
          window.location = 'semantic-search?semantic=true&syntax=man&expression=' + encodeURI(mseditor.getValue());
          window.onload = function () {
          //saveMSQuery(mscur);     // for query history -- added by @gs
          };   
          }
      
		  }} ,{
			xtype : 'button',
	 		text : 'Clear query',
		  handler: function(event) {
			   form.getForm().reset();
         mseditor.setValue("");           //handler if codemirror is used - added by @gs
	       conditionalRadioButtons.disable();
         conditionalRadioButtons.reset();
			}, 
      }],
      
    renderTo: Ext.getBody()
	});
  
     
   var mspressed = false;
   var mshistoryBtnHandler = Ext.getCmp('mshistoryButton').on('click', function(event) {
        mspressed = true;
        mshistoryPanel.setPosition(0,0);  // for covering queryPanel's body
         if (!mshistoryPanel.isVisible()){ 
            mshistoryPanel.setVisible(true);
         }
         else{
            mshistoryPanel.setVisible(false);
         }
      });
 
  Ext.getCmp('mshistoryButton').on('mouseout', function(event) {
    mspressed = false;
  });


  
  var mseditor = CodeMirror.fromTextArea(document.getElementById("msquery"), { 
       mode: "manchestersyntax",
       tabMode: "indent",
       lineWrapping: true,
       matchBrackets: true,     
       placeholder: "using the \"Add term\" button put all pieces together or type your type DL query dirctly",
       extraKeys: {
          // @gs - star searching on pressing enter
          "Enter": function(event) {if (mseditor.getValue().length>0) { window.location = 'semantic-search?semantic=true&syntax=man&expression=' +encodeURI(mseditor.getValue()); } },
          "Ctrl-Space": "autocomplete"
       }
   });
   mseditor.setSize(null,30); // height 30 --> 2 lines 
   mseditor.setValue(expression);     // added by @gs : keeps query when interface is refreshed, after "searh" is pressed
      
   myMS_query = mseditor.getValue();
  
/* @gs : shows hint when clicking Ctrl+Space 
  CodeMirror.commands.autocomplete = function(cm) {
     CodeMirror.showHint(cm, CodeMirror.hint.ms);
  };


  var orig = CodeMirror.hint.ms;
  
  CodeMirror.hint.ms = function(cm) {
   var inner = orig(cm) //|| {from: cm.getCursor(), to: cm.getCursor(), list: []};
    
   var countObj = dataObjectProperties.size(); 
   var countCl = dataClasses.size();  
             
    for (var i = 0; i < countObj; i++) 
        inner.list.push(dataObjectProperties[i][0]);   
    
    for (var i = 0; i < countCl; i++) 
        inner.list.push(dataClasses[i][0]);   
    
    return inner
};          */
   
////////////////////////  SPARQL end-point /////////////////////////////////

  spStore = new Ext.data.SimpleStore({    // now reads global variable spStore
        fields: ['query'],
        data: Ext.state.Manager.get('SPStore', []) 
  });

        
/*   function saveSPQuery(query) { 
      if (Ext.isEmpty(query)) 
         return;

      spStore.clearFilter(false);
         
      if ((spStore.findExact('query', query) < 0)) {  // replaced "find" with "findExact"

         var data = [[query, 'query']];   // σωστό!
         var count = spStore.getTotalCount(); 
         var limit = count >= 10? 9: count;  //@gs "count>=10"! and not "count>10"
                   
          for (var i = 0; i < limit; i++) {
              data.push([spStore.getAt(i).get('query')]); 
          }
                                            
          if (Ext.state.Manager.getProvider()) 
             Ext.state.Manager.set('SPStore', data); 
          
           spStore.removeAll();
           spStore.loadData(data);     
        }
      
    }	  */      

var historyGrid = new Ext.grid.GridPanel({
    id: 'historyGrid',
    frame: false, 
    store: spStore,
    border: true,
    bodyStyle: 'text-align:left;',
    viewConfig: {
      forceFit: true,       // for column to fit exactly the panel width
      scrollOffset: 0,
      headersDisabled: true //@gs - for not highlighting header on mouse over       
    },     
    columns: [
            {id:'history', header: 'Query History', dataIndex:'query', sortable: false, resizable: false}   // query: same as spstore
    ],
    //sm: new Ext.grid.RowSelectionModel({singleSelect:true}),
    enableHdMenu: false,      // disables header menu
    height: 80, //  the height of its container
    hideHeaders: false,
    stripeRows: true,
});   
  
  var historyPanel = new Ext.Panel({
    id: 'historyPanel',
    floating: true, // for not being like having transparency!
    frame: false,
    boxMaxWidth : 800,
    boxMinWidth : 200,
    //border: false,  // false because we added bodyStyle at grid
    bodyCfg: { //@gs DO NOT DELETE - places border to the history panel
        cls: 'need-border-class x-panel-body',  // Default class not applied if Custom element specified 
        style: {'border':'1px solid #C0C0C0;'},
    }, 
    layout:{
        type:'fit',
        align:'stretch'
    },
    style: {              // always on top of panels
          'z-index': 15000
    },
    height: 80, //a bit less than queryPanel
    items: [historyGrid],
    hidden: true,
    x: 25,
    y: 0,
    shadow: true
 });     
 

  var sparqlPanel = new Ext.Panel({
    id: 'sparqlPanel',
    frame: false,
    border: false,
    height: 100,
    hidden: false,
    items: [{
      style: 'font-size:12px; text-align:left;',
      html: '<textarea id="spquery" name="sp" style="display:none;" autocomplete="on" draggable="true">PREFIX a: &lt;http://www.w3.org/2000/10/annotation-ns#> \n# Comment! \n\nSELECT ?x ?y ?z \nWHERE { ?x ?y ?z .}</textarea>',
      } ],
    x: 0,
    y: 0
 });
  
//  console.log(document.getElementById("sparqlPanel").getWidth());
  
  var spWithHistory = new Ext.Panel({
    id: 'spWithHistory',
    frame: false,
    border: true,
    height: 100,
    layout: 'absolute',
    bodyCfg: { //@gs DO NOT DELETE - hides double border
        cls: 'no-border-class x-panel-body x-panel-noheader',  // Default class not applied if Custom element specified 
        style: {'border-top':'none;'},
    },   
    flex: 9, //its container (queryPanel) has layout of type "hbox"
    items: [sparqlPanel, historyPanel],
 });
 
   var queryPanel = new Ext.Panel({
    id: 'queryPanel',
    frame: false,
    border: true,
    height: 100,
    layout: { // 1
        type: 'hbox',
        align: 'stretch'
    },
    bodyCfg: {
        cls: 'no-border-class .x-panel-body',  // Default class not applied if Custom element specified 
        style: {'body-style':'none;'}
    }, 
    items: [spWithHistory, {
      xtype: 'button',
      id: 'historyButton',
      iconCls: 'add24',
      arrowAlign: 'bottom',
      height: 100, // for absolute layout
      width: 24,  // for absolute layout - do not change
      floating: true,
      style: {              // always on top of panels
          'z-index': 3
      } ,
      flex: 1, 
      menu:[], // fake menu, so as to show arrow on the button
      handler: historyBtnHandler,   // see below
    }]
 });
   var pressed = false;
   var historyBtnHandler = Ext.getCmp('historyButton').on('click', function(event) {
        pressed = true;
        historyPanel.setPosition(25,0);  // for covering spWithHistory's body
         if (!historyPanel.isVisible()){ 
            historyPanel.setVisible(true);
         }
         else{
            historyPanel.setVisible(false);
         }
      });
 
  Ext.getCmp('historyButton').on('mouseout', function(event) {
    pressed = false;
  });
 
 
  var queryLabelSP = new Ext.form.Label({
    style: 'padding:5px;', 
    html : "Query:"
  });

  var reasonerLabelSP = new Ext.form.Label({
    html : "Loaded reasoner is " + reasonerValue
  });
  
  var advancedPanelSP = new Ext.Panel( {
    title : 'SPARQL Query',
    frame: false, 
    bodyBorder: false,
    labelWidth : 70,
		monitorValid : true,
    bodyStyle : 'font:12px tahoma,arial,helvetica,sans-serif; padding:10px; text-align:right;',		
    buttonAlign : 'center',
    autoHeight : true,
    //@gs - the following bodyCFG was added so as to remove the border that surrounds
    //the items of this Panel (excluding buttons) - this border appears becaues we used the renderTo option
  	bodyCfg: {
        cls: 'no-border-class .x-panel-body',  // Default class not applied if Custom element specified 
        style: {'body-style':'none;'}
    }, 
    items: [
      queryPanel, reasonerLabelSP   //@@@@@@@@@@@@@@@@@@@@@
    ],
    buttons : [ {
			xtype : 'button',
			text : 'Search',
      handler: function(event) {
        if (speditor.getValue().length>0) {
					var cur = speditor.getValue();
          saveSPQuery(cur);     // ############## TO DELETE - NEED TO BE REBLACED BY METHOD CALL IN JSP
          historyPanel.setVisible(false);
          sparqlPanel.setVisible(true);
         // advancedPanelSP.doLayout();
         // @GS: need to call the sparql specific servlet
        }        
			 }     
		  }, {
			xtype : 'button',
			text : 'Clear query',
			handler: function(event) {
			     historyPanel.setVisible(false);
           sparqlPanel.setVisible(true);
           speditor.setValue("");
			}
		}],
    renderTo: document.body
	});

  /* handler for MS history */
  historyGrid.on("rowclick", function(e){  
      historyPanel.setVisible(false);
      sparqlPanel.setVisible(true);    
      var row = historyGrid.getSelectionModel().getSelected();
      var record = row.get('query');  // Get the Record
      speditor.setValue(record);     
      historyGrid.getSelectionModel().clearSelections();
      historyGrid.getView().focusRow(0);
  });   
  
  /* handler for SPARQL history */
  mshistoryGrid.on("rowclick", function(e){  
      mshistoryPanel.setVisible(false);
      msqueryPanel.setVisible(true);    
      var row = mshistoryGrid.getSelectionModel().getSelected();
      var record = row.get('query');  // Get the Record
      mseditor.setValue(record);     
      mshistoryGrid.getSelectionModel().clearSelections();
      mshistoryGrid.getView().focusRow(0);
  });   
     
  
 var speditor = CodeMirror.fromTextArea(document.getElementById("spquery"), { 
        mode: "application/x-sparql-query",
        tabMode: "indent",
        lineWrapping: true,
        matchBrackets: true,
        lineNumbers: "on",  
        placeholder: "write your SPARQL query",
        extraKeys: {"Ctrl-Space": "autocomplete"}
   });
   speditor.setSize(null,100);      //(width, height)
  // speditor.setValue(expression);     // added by @gs : keeps query when search button is pressed
   mySP_query = speditor.getValue(); // 
     
	var optionsPanel = new Ext.Panel( {
		id: 'options-panel',
    title : 'Options',
		bodyStyle : 'padding:5px',
		buttonAlign : 'center',
		autoHeight : true,
    //tabTip : 'change ontology and/or reasoner',             //added by @GS
		items : [ optionsForm ]
	});

	var tabs = new Ext.TabPanel( {
		renderTo : 'destino',
		activeTab : 0,
		deferredRender: false,
		forceLayout: true,
		items : [ searchPanel, advancedPanelSP, optionsPanel ],
  });

 
 // Ext.util.Observable.capture(Ext.getCmp(historyGrid.id), function(evname) {console.log(evname, arguments);})

  document.onclick = docclickhandler; 
  
  function docclickhandler() {  
      if(!pressed)
         historyPanel.setVisible(false);
      if(!mspressed)
         mshistoryPanel.setVisible(false);
  }

  termField.focus();  
}

/* Handles LD facility and DBpedia lookup service */

function loadXMLDoc(name, elem){

 var cords = elem.getBoundingClientRect();  
 var mouseY = Math.round (cords.top);  // top
 var mouseX = Math.round (cords.left);  // left

  document.getElementById('querytooltip').style.top = mouseY + "px";
  document.getElementById('querytooltip').style.left = mouseX + "px";
  document.getElementById('querytooltip').style.display = 'block';
  
  var keyword=name.replace(" ","_"); 

  var xmlhttp;
  if (window.XMLHttpRequest)
	  {// code for IE7+, Firefox, Chrome, Opera, Safari
	   xmlhttp=new XMLHttpRequest();
	  }
	else
	  {// code for IE6, IE5
	   xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  }
  
	var query_url = "http://lookup.dbpedia.org/api/search.asmx/KeywordSearch?QueryString="+keyword;
	xmlhttp.open("GET",query_url,false);
	xmlhttp.send();
	xmlDoc=xmlhttp.responseXML;
	
	var x=xmlDoc.getElementsByTagName("Result");

  var result="";
	var resultURI="";
  
	if (x.length!=0){
		for (i=0;i<x.length;i++)
		{ 
      resultURI=x[i].getElementsByTagName("URI")[0].childNodes[0].nodeValue;
		  result=result+'<a href="'+resultURI+'" target="_blank">'+resultURI+'</a>&nbsp;</br>';
		}
		document.getElementById("querytooltip").innerHTML="<a href='javascript:hideTooltip()' style='float:right;'><b>[X]</b></a>";
    document.getElementById("querytooltip").innerHTML=document.getElementById("querytooltip").innerHTML + result;
    
	}
	else{
		document.getElementById("querytooltip").innerHTML="<a href='javascript:hideTooltip()' style='float:right;'><b>[X]</b></a>";
    document.getElementById("querytooltip").innerHTML=document.getElementById("querytooltip").innerHTML + "No results"+"&nbsp;";
	}
}

function hideTooltip(){
  document.getElementById("querytooltip").style.display = 'none';
  
} 

 // for query history --> if query cannot be parsed, query is not stored



function saveMSQuery() { 
   
      var query = myMS_query;
      
      if (Ext.isEmpty(query)) 
         return;

      msStore.clearFilter(false);

      //var foundExep = checkException();  //checks if query was not parsed correctly
      //console.log(foundExep); 
      if (msStore.findExact('query', query) < 0) {  // replaced "find" with "findExact"

         var msdata = [[query, 'query']];   // σωστό!
         var count = msStore.getTotalCount(); 
         var limit = count >= 10? 9: count;  //@gs "count>=10"! and not "count>10"
         
          for (var i = 0; i < limit; i++) {
              msdata.push([msStore.getAt(i).get('query')]); 
          }
                                            
          if (Ext.state.Manager.getProvider()) 
             Ext.state.Manager.set('MSStore', msdata); 
          
           msStore.removeAll();
           msStore.loadData(msdata);     
        }
      
    }
    

            
function saveSPQuery() { 
      if (Ext.isEmpty(query)) 
         return;

      var query = mySP_query;
      spStore.clearFilter(false);
         
      if ((spStore.findExact('query', query) < 0)) {  // replaced "find" with "findExact"

         var data = [[query, 'query']];   // σωστό!
         var count = spStore.getTotalCount(); 
         var limit = count >= 10? 9: count;  //@gs "count>=10"! and not "count>10"
                   
          for (var i = 0; i < limit; i++) {
              data.push([spStore.getAt(i).get('query')]); 
          }
                                            
          if (Ext.state.Manager.getProvider()) 
             Ext.state.Manager.set('SPStore', data); 
          
           spStore.removeAll();
           spStore.loadData(data);     
        }
      
    }