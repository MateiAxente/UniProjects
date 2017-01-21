
interface Command {
	void execute(Repository rep) throws MyInvalidPathException, MyNotEnoughSpaceException;
	void execute(Directory dir) throws MyInvalidPathException, MyNotEnoughSpaceException;
	void execute(File file);
}
