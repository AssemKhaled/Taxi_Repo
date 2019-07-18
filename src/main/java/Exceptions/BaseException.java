package Exceptions;

public class BaseException extends Exception  {
	private static final long serialVersionUID = -5634361929539644915L;
	String description = "General server error";
	int status = 0;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BaseException(String description, int status) {
		super();
		this.description = description;
		this.status = status;
	}
}
