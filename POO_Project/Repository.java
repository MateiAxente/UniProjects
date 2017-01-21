
interface Repository {
	public void accept(Command c) throws MyInvalidPathException, MyNotEnoughSpaceException;
	public String toString();
	public String toList();
	public String toString(int r);
	public String toList(int r);
}
