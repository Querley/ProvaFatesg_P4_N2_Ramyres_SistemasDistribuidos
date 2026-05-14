package com.fatesg.web_api.apis;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

import com.fatesg.biblioteca.dtos.FuncionarioDto;
import com.fatesg.biblioteca.interfaces.ServidorDeDadosFuncionarioInterface;
import com.fatesg.web_api.configs.RmiConfig;

public class ServidorDeDadosFuncionarioApi implements ServidorDeDadosFuncionarioInterface {

  private ArrayList<ServidorDeDadosFuncionarioInterface> servidores;

  public void Conectar() {
    if (this.servidores != null && !this.servidores.isEmpty()) {
      this.servidores.clear();
    } else {
      this.servidores = new ArrayList<>();
    }

    AddServico(RmiConfig.RMI_SERVICE_NAME, RmiConfig.RMI_HOST, RmiConfig.RMI_PORT);

    AddServico(RmiConfig.RMI_SERVICE_NAME, RmiConfig.RMI_HOST_SECOND, RmiConfig.RMI_PORT_SECOND);

    if (this.servidores.isEmpty()) {
      System.err.println("Nenhum servidor de funcionários disponível.");
    }
  }

  @Override
  public List<FuncionarioDto> listarFuncionarios(int limite, int offset) throws RemoteException {
    return this.getFuncionarios(limite, offset, true);
  }

  @Override
  public FuncionarioDto obterFuncionarioPorId(int id) throws RemoteException {
    return this.getFuncionario(id, true);
  }

  @Override
  public int obterQtdeFuncionarios() throws RemoteException {
    return this.getQtdeFuncionarios(true);
  }

  private void AddServico(String serviceName, String host, int port) {
    try {
      Registry registry = LocateRegistry.getRegistry(host, port);

      var servico = (ServidorDeDadosFuncionarioInterface) registry.lookup(serviceName);

      this.servidores.add(servico);

    } catch (RemoteException e) {
      System.err.println("Erro na comunicação com servidor de funcionários");
    } catch (NotBoundException e) {
      System.err.println("Serviço de funcionários não encontrado");
    }
  }

  private List<FuncionarioDto> getFuncionarios(int limite, int offset, boolean firstTime) {
    for (var s : this.servidores) {
      try {
        return s.listarFuncionarios(limite, offset);

      } catch (RemoteException e) {
        System.err.println("Erro ao listar funcionários");
      }
    }

    if (firstTime) {
      System.out.println("Tentando reconectar aos servidores...");
      this.Conectar();
      return this.getFuncionarios(limite, offset, false);
    }

    throw new RuntimeException("Erro ao obter funcionários");
  }

  private FuncionarioDto getFuncionario(int id, boolean firstTime) {
    for (var s : this.servidores) {
      try {
        return s.obterFuncionarioPorId(id);

      } catch (RemoteException e) {
        System.err.println("Erro ao obter funcionário");
      }
    }

    if (firstTime) {
      System.out.println("Tentando reconectar aos servidores...");
      this.Conectar();
      return this.getFuncionario(id, false);
    }

    throw new RuntimeException("Erro ao obter funcionário");
  }

  private int getQtdeFuncionarios(boolean firstTime) {
    for (var s : this.servidores) {
      try {
        return s.obterQtdeFuncionarios();

      } catch (RemoteException e) {
        System.err.println("Erro ao obter quantidade de funcionários");
      }
    }

    if (firstTime) {
      System.out.println("Tentando reconectar aos servidores...");
      this.Conectar();
      return this.getQtdeFuncionarios(false);
    }

    throw new RuntimeException("Erro ao obter quantidade de funcionários");
  }
}
