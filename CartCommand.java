
public interface CartCommand {
void execute();
String getName();
double getPrice();
default String getDescription() {
	return getName() + " $" + String.format("%.2f",getPrice());
}
}
