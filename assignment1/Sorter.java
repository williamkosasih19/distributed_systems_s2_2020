import java.rmi.Remote;
import java.rmi.RemoteException;

import java.util.UUID;

public interface Sorter extends Remote {
  public void pushValue(int val, UUID uuid) throws RemoteException;
  public void pushOperator(String operator, UUID uuid) throws RemoteException;
  public int pop(UUID uuid) throws RemoteException;
  public boolean isEmpty(UUID uuid) throws RemoteException;
  public int delayPop(int millis, UUID uuid) throws RemoteException;
}