class Value{

	private int value;
	private final boolean isVar;

	public Value(int n, boolean b){
		this.value = n;
 		this.isVar = b;
	}

	public int getVal(){
		return this.value;
	}

	public boolean isVar(){
		return this.isVar;
	}
}
