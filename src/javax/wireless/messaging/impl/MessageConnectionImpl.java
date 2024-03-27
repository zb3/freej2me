/*
 * Copyright 2018 Nikita Shakarun
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package javax.wireless.messaging.impl;

import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;

public class MessageConnectionImpl implements MessageConnection {
	private MessageListener listener;
	private String address;
	private boolean noMessages;

	public MessageConnectionImpl(String name) {
		String address = name.substring("sms://".length());
		this.address = address;
	}

	@Override
	public Message newMessage(String type) {
		return newMessage(type, address);
	}

	@Override
	public Message newMessage(String type, String address) {
		Message message;
		if (type.equals(TEXT_MESSAGE)) {
			message = new TextMessageImpl(address, 0);
		} else if (type.equals(BINARY_MESSAGE)) {
			message = new BinaryMessageImpl(address, 0);
		} else {
			throw new IllegalArgumentException("Message type is invalid: " + type);
		}
		return message;
	}

	@Override
	public int numberOfSegments(Message message) {
		return 1;
	}

	@Override
	public Message receive() {
		while (noMessages) {
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		TextMessage message = new TextMessageImpl(address, System.currentTimeMillis());
		message.setPayloadText("ok");
		noMessages = true;
		return message;
	}

	@Override
	public void send(Message message) {
		if (message == null) {
			throw new NullPointerException();
		}

		// for each message we send, we want one to be available to be received
		noMessages = false;

		if (listener != null) {
			listener.notifyIncomingMessage(this);
		}
	}

	@Override
	public void setMessageListener(MessageListener listener) {
		this.listener = listener;
	}

	@Override
	public void close() {
	}
}
