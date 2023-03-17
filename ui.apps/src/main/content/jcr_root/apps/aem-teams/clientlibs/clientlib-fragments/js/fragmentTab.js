/*
 * ***********************************************************************
 * MIT License
 *
 * Copyright (c) 2023 Diana Henrickson (diana.henrickson@bounteous.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * ***********************************************************************
 */

(function(document, Granite, $) {
    "use strict";

    window.onload = function() {

        function setTabs(){
   			//var tabRoot = document.getElementsByClassName("_coral-Tabs--vertical");
        	var tabRoot = $.find('._coral-Tabs--vertical');
        	var tabElement = document.createElement('coral-tab');
        	tabElement.setAttribute('icon','chat');
        	tabElement.setAttribute('title','history');
        	tabElement.setAttribute('aria-label','history');
        	tabElement.setAttribute('class','_coral-Tabs-item');
			tabElement.setAttribute('role','tab');
        	tabElement.setAttribute('id','coral-id-chatui-tab');

        	tabRoot[tabRoot.length -1].appendChild(tabElement);

			//var panelRoot = document.getElementsByClassName("_coral-PanelStack");
        	var panelRoot = $.find('._coral-PanelStack');
        	var panelElement = document.createElement('coral-panel');
        	panelElement.setAttribute('id','coral-id-chatui-tab');
        	var panelContentElement = document.createElement('coral-panel-content');
        	panelElement.appendChild(panelContentElement);

			var contentDiv = document.createElement('div');
			contentDiv.setAttribute('class','sidepanel-tab sidepanel-tab-content');
        	contentDiv.setAttribute('data-headertitle','AEM Teams');

        	panelContentElement.appendChild(contentDiv);

			var titleHeading = document.createElement('h2');
        	titleHeading.setAttribute('class','coral-Heading coral-Heading--2 sidepanel-tab-title');
        	titleHeading.innerHTML='AEM Teams';
        	var contentPanelDiv = document.createElement('div');
			var contentPath = window.location.href;
        	$(contentPanelDiv).load("/apps/aem-teams/sidepanel/content/chatpanel/items/chatui.html?path="+contentPath);
        	contentDiv.appendChild(titleHeading);
        	contentDiv.appendChild(contentPanelDiv);

        	titleHeading.setAttribute('class','coral-Heading coral-Heading--2 sidepanel-tab-title');
        	titleHeading.innerHTML='AEM Teams';

			panelRoot[panelRoot.length -1].appendChild(panelElement);

			}
        	function setChatUi(){
				var teamsLinkBaseUri = 'msteams:/l/chat/0/0';
   				if ($('#main-chat-panel').length > 0) {

        			var accordionItems = $.find('.cmp-chat-ui__accordion');
        			$(accordionItems).each(function (i, item) {
            			$(item).click(function() {
                			$(item).toggleClass('active');
                			var panel = $(item).next();
                			if ($(panel).css('display') === 'block') {
                    			$(panel).css('display', 'none');
                			} else {
                    			$(panel).css('display', 'block');
                			}
            			});
        			});

        		var userSelect = $('input.cmp-chat-ui__group-add-input');
        		userSelect.change(function () {
            		$('#teamsgroupchat').prop('disabled', userSelect.filter(':checked').length < 1);
        		});
        		userSelect.change();

        		var checkboxes = $.find('.cmp-chat-ui__group-add-checkbox');
        		$(checkboxes).each(function(i, checkbox) {
            		$(checkbox).click(function(event) {
                		event.stopImmediatePropagation();
            		});
        		});

        		var teamsGroupChat = $('#teams-group-chat');
        		$(teamsGroupChat).click(function(event) {
            		event.preventDefault();
            		var selected = [];
            		$.each($('input[name="selectedUser"]:checked'), function() {
                		var div = this.closest('div');
                		var { teamsid } = div.dataset;
                		selected.push(teamsid);
            		});
            		var { tenantid, page } = this.dataset;
            		var teamsGroupLink = `${teamsLinkBaseUri}?users=${selected.join(',')}&tenantId=${tenantid}&message=In regards to your edits on ${page}`;
            		window.open(teamsGroupLink, '_blank');
        		});

        		var teamsIndividualChat = $.find('.cmp-chat-ui__teams-chat-btn--individual');
        		$(teamsIndividualChat).each(function(i, chatButton) {
            		$(chatButton).click(function(event) {
                		event.preventDefault();
                		var { tenantid, teamsid, page } = this.dataset;
                		var teamsLink = `${teamsLinkBaseUri}?users=${teamsid}&tenantId=${tenantid}&message=In regards to your edits on ${page}`;
                		window.open(teamsLink, '_blank');
            		});
        		});

        		var removeActiveView = function(currentButton) {
            		var activeView = $.find('.cmp-chat-ui__component--selectable[data-status="on"]');
            		$(activeView).each(function(i, viewButton) {
                		$(viewButton).attr('data-status', 'off');
                		$(viewButton).removeClass('selected');
                		var currentComponentPath = currentButton.dataset.contentitem;
                		var activeComponentPath = viewButton.dataset.contentitem;
                		if (currentComponentPath !== activeComponentPath) {
                    		var activeDestination = $(`[data-path="${activeComponentPath}"]`);
                    		$(activeDestination).removeClass('selected-area');
                		}
            		});
        		};

        		var viewComponentButton = $.find('.cmp-chat-ui__component--selectable');
        		$(viewComponentButton).each(function(i, button) {
            		$(button).click(function() {
                		var componentPath = button.dataset.contentitem;
                		var destination = $(`[data-path="${componentPath}"]`);
                		$(destination).addClass('selected-area');
                		$(destination).css('z-index', '50');
                		var { status } = this.dataset;
                		if (status === 'off' || !status) {
                    		removeActiveView(button);
                    		$(button).attr('data-status', 'on');
                    		$(button).addClass('selected');
                    		$(destination)[0].scrollIntoView();
                		} else {
                    		$(button).attr('data-status', 'off');
                    		$(button).removeClass('selected');
                    		$(destination).removeClass('selected-area');
                		}
            		});
        		});

				var modalButtons = $.find('.modalButton');
        		$(modalButtons).each(function(i, modalButton) {
            	$(modalButton).click(function() {
                var modalId = modalButton.dataset.id;
                var modal = $('#'+modalId);
				if ($(modal).css('display') === 'block') {
                    $(modal).css('display', 'none');
					$(modalButton).css('background-color','#fff');
					$(modalButton).css('color','#4b4b4b');
                } else {
                    $(modal).css('display', 'block');
				    $(modalButton).css('background-color','#4b4b4b');
					$(modalButton).css('color','#fff');
                }
                
            });
        });
    			}

			}

		$.ajax({
   			url:setTabs(),
   			success:function(){
   				setChatUi();
			}
		})




  };
})(document, Granite, Granite.$);