package http;

public enum HttpStatus {
	OK, 
	NOT_FOUND; 
	
	@Override
	public String toString() {
		switch (this) {
		case OK:
			return "200 OK";		

		default:
			return "404 NOT FOUND";
		}
	}

}
