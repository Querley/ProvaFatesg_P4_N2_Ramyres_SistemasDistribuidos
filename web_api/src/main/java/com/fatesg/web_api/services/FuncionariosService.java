package com.fatesg.web_api.services;

import java.rmi.RemoteException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fatesg.biblioteca.dtos.FuncionarioDto;
import com.fatesg.biblioteca.interfaces.ServidorDeDadosFuncionarioInterface;

import com.fatesg.web_api.apis.ServidorDeDadosFuncionarioApi;

@Service
public class FuncionariosService {
  private ServidorDeDadosFuncionarioInterface servico;

  public FuncionariosService() {
    var api = new ServidorDeDadosFuncionarioApi();

    api.Conectar();

    this.servico = api;
  }

  public List<FuncionarioDto> getFuncionarios(int limite, int offset) {
    if (servico == null) {
      throw new RuntimeException(
          "Serviço de dados de funcionários não está disponível. Verifique se o registro RMI está conectado.");
    }
    try {
      List<FuncionarioDto> funcionarios = servico.listarFuncionarios(limite, offset);
      return funcionarios;
    } catch (RemoteException e) {
      System.err.println("Erro na comunicação com o servidor no getFuncionarios:");
      e.printStackTrace();
      throw new RuntimeException("Erro na comunicação com o servidor ao obter funcionários", e);
    }
  }

  public FuncionarioDto getFuncionarioById(int id) {
    if (servico == null) {
      throw new RuntimeException(
          "Serviço de dados de funcionários não está disponível. Verifique se o registro RMI está conectado.");
    }
    try {
      FuncionarioDto funcionario = servico.obterFuncionarioPorId(id);
      return funcionario;
    } catch (RemoteException e) {
      System.err.println("Erro na comunicação com o servidor no getFuncionarioById:");
      e.printStackTrace();
      throw new RuntimeException("Erro na comunicação com o servidor ao obter funcionário", e);
    }
  }
}
