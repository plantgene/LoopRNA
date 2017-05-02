package loops;

import java.util.ArrayList;

public class Loop
{
	private int name;
	private int start;
	private int end;
	private int level;
	private int size;
	private int[] elements = new int[2]; // 1 = ( $$ 0 = . $$ ) not represented

	private ArrayList<int[]> numStruct;
	private ArrayList<Loop> owners = new ArrayList<>();
	private ArrayList<Loop> owned = new ArrayList<>();



	public Loop(int name, int start, int end, ArrayList<int[]> numStruct)
	{
		this.name = name;
		this.start = start;
		this.end = end;
		this.level = Integer.MAX_VALUE;
		this.numStruct = numStruct;
		this.elements = elementsFinder();
		this.size = this.end - this.start;
	}



	public int[] elementsFinder()
	{
		/*int[] elements = new int[2];

		for(int index = this.start; index <= this.end; index++)
		{
			int[] element = this.numStruct.get(index);
			switch(element[0])
			{
				case 0:
				{
					elements[0]++;
					break;
				}
				case 1:
				{
					elements[1]++;
					break;
				}
				case -1:
					break;
			}
		}

		return elements;*/
		
		return this.elementsFinder(this.start, this.end);
	}
	
	
	
	public int[] elementsFinder(int start, int stop)
	{
		int[] elements = new int[2];

		for(int index = start; index <= stop; index++)
		{
			int[] element = this.numStruct.get(index);
			switch(element[0])
			{
				case 0:
				{
					elements[0]++;
					break;
				}
				case 1:
				{
					elements[1]++;
					break;
				}
				case -1:
					break;
			}
		}

		return elements;
	}



	public int getName()
	{
		return name;
	}



	public void setName(int name)
	{
		this.name = name;
	}



	public int getStart()
	{
		return start;
	}



	public void setStart(int start)
	{
		this.start = start;
	}



	public int getEnd()
	{
		return end;
	}



	public void setEnd(int end)
	{
		this.end = end;
	}



	public int getLevel()
	{
		return level;
	}



	public void setLevel(int level)
	{
		this.level = level;
	}



	public int[] getElements()
	{
		return elements;
	}



	public void setElements(int[] elements)
	{
		this.elements = elements;
	}



	public ArrayList<Loop> getOwners()
	{
		return owners;
	}



	public void setOwners(ArrayList<Loop> owners)
	{
		this.owners = owners;
	}



	public ArrayList<Loop> getOwned()
	{
		return owned;
	}



	public void setOwned(ArrayList<Loop> owned)
	{
		this.owned = owned;
	}



	private int ownership(Loop owner, boolean listType)
	{
		ArrayList<Loop> list;
		if(listType)
			list = this.owners;
		else
			list = this.owned;

		for(int index = 0; index < list.size(); index++)
		{
			if(list.get(index) == owner) return index;
		}

		return -1;
	}



	public int isOwnedBy(Loop owner)
	{
		return ownership(owner, true);
	}



	public int owns(Loop owner)
	{
		return ownership(owner, false);
	}



	public void addOwner(Loop owner)
	{
		this.owners.add(owner);
	}



	public void removeOwner(Loop owner)
	{
		this.owners.remove(owner);
	}



	public void addOwned(Loop owned)
	{
		this.owned.add(owned);
	}



	public void removeOwned(Loop owned)
	{
		this.owned.remove(owned);
	}



	public int getSize()
	{
		return(this.size);
	}
	
	
	
	public void setSize(int size)
	{
		this.size = size;
	}



	public void loopPrinter()
	{
		System.out.print("Loop ");
		System.out.print(this.name);
		System.out.print(" start ");
		System.out.print(this.start);
		System.out.print(" end ");
		System.out.print(this.end);
		System.out.print(" owns ");
		if(this.owned.size() == 0)
			System.out.print("nothing ");
		else
		{
			for(int index = 0; index < this.owned.size(); index++)
			{
				System.out.print(this.owned.get(index).getName());
				System.out.print(" ");
			}
		}
		System.out.print("is owned by ");
		if(this.owners.size() == 0)
			System.out.print("nothing ");
		else
		{
			for(int index = 0; index < this.owners.size(); index++)
			{
				System.out.print(this.owners.get(index).getName());
				System.out.print(" ");
			}
		}
		System.out.print("and is level ");
		System.out.print(this.level);
		System.out.println();
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append("Loop ");
		builder.append(this.name);
		builder.append(" start ");
		builder.append(this.start);
		builder.append(" end ");
		builder.append(this.end);
		builder.append(" owns ");
		if(this.owned.size() == 0) builder.append("nothing ");
		else
		{
			for(int index = 0; index < this.owned.size(); index++)
			{
				builder.append(this.owned.get(index).getName());
				builder.append(" ");
			}
		}
		builder.append("is owned by ");
		if(this.owners.size() == 0)	builder.append("nothing ");
		else
		{
			for(int index = 0; index < this.owners.size(); index++)
			{
				builder.append(this.owners.get(index).getName());
				builder.append(" ");
			}
		}
		builder.append("and is level ");
		builder.append(this.level);
		
		return builder.toString();
	}
}
