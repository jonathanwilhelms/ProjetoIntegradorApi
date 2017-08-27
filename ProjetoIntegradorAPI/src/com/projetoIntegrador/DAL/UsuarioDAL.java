package com.projetoIntegrador.DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.projetoIntegrador.Conexao.Conexao;
import com.projetoIntegrador.Enumerador.EPerfil;
import com.projetoIntegrador.Exceptions.BDException;
import com.projetoIntegrador.Exceptions.EErrosBD;
import com.projetoIntegrador.Model.UsuarioModel;
import com.projetoIntegrador.Util.Funcoes;

public class UsuarioDAL {

	public static Integer Inserir(UsuarioModel model) throws BDException {
		Connection conexao = Conexao.getConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("INSERT INTO USUARIO (NOME, EMAIL, SENHA, PERFIL, COD_SETOR)"
                                                            +"VALUES (?, ?, ?, ?, ?);");
			
			pst.setString(1, model.getNome());
			pst.setString(2, model.getEmail());
			pst.setString(3, model.getSenha());
			pst.setInt(4, model.getPerfil().getIndex());
			
			if (model.getCodSetor() < 0)
			{
				pst.setNull(5, Types.INTEGER);
			}
			else
			{
				pst.setInt(5, model.getCodSetor());				
			}
			
			pst.executeUpdate();
			return Funcoes.getId("USUARIO");
		} catch (Exception e) {
			throw new BDException(EErrosBD.INSERE_DADO, e.getMessage());
		} finally {
			Conexao.closeConexao();
		}
	}

	public static UsuarioModel Buscar(Integer Id) throws BDException {
		Connection conexao = Conexao.getConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("SELECT * FROM USUARIO WHERE ID = ?;");
			pst.setInt(1, Id);
			ResultSet rs = pst.executeQuery();
			if (rs.first()) {
				return new UsuarioModel(
						rs.getInt("ID"), 
						rs.getString("NOME"), 
						rs.getString("EMAIL"), 
						rs.getString("SENHA"), 
						EPerfil.getEnum(rs.getInt("PERFIL")), 
						rs.getInt("COD_SETOR"));
			}
			return null;
 		} catch (Exception e) {
 			throw new BDException(EErrosBD.CONSULTA, e.getMessage());
 		} finally {
 			Conexao.closeConexao();
 		}
	}

	public static Boolean Alterar(UsuarioModel model) throws BDException {
		Connection conexao = Conexao.getConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("UPDATE USUARIO SET NOME = ?, EMAIL = ?, SENHA = ?, PERFIL = ?, COD_SETOR = ? WHERE ID = ?;");
			pst.setString(1, model.getNome());
			pst.setString(2, model.getEmail());
			pst.setString(3, model.getSenha());
			pst.setInt(4, model.getPerfil().getIndex());
			
			if (model.getCodSetor() < 0)
			{
				pst.setNull(5, Types.INTEGER);
			}
			else
			{
				pst.setInt(5, model.getCodSetor());				
			}
			
			
			pst.setInt(6, model.getId());
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new BDException(EErrosBD.ATUALIZA, e.getMessage());
		} finally {
			Conexao.closeConexao();
		}
	}

	public static Boolean Deleter(Integer Id) throws BDException {
		Connection conexao = Conexao.getConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("DELETE FROM USUARIO WHERE ID = ?;");
			pst.setInt(1, Id);
			return pst.executeUpdate() > 0;
		} catch (Exception e) {
			throw new BDException(EErrosBD.EXCLUI, e.getMessage());
		} finally {
			Conexao.closeConexao();
		}
	}
	
	public static UsuarioModel VerificarLogin(String email, String senha) throws BDException 
	{
		Connection conexao = Conexao.getConexao();
		
		try {
			PreparedStatement pst = conexao.prepareStatement("SELECT * FROM USUARIO WHERE EMAIL = ? AND SENHA = ?;");
			pst.setString(1, email);
			pst.setString(2, senha);
			ResultSet rs = pst.executeQuery();
			if (rs.first()) {
				return new UsuarioModel(
						rs.getInt("ID"), 
						rs.getString("NOME"), 
						rs.getString("EMAIL"), 
						rs.getString("SENHA"), 
						EPerfil.getEnum(rs.getInt("PERFIL")), 
						rs.getInt("COD_SETOR"));
			}
			return null;
 		} catch (Exception e) {
 			throw new BDException(EErrosBD.CONSULTA, e.getMessage());
 		} finally {
 			Conexao.closeConexao();
 		}
	}
	
	public static List<UsuarioModel> Listar() throws BDException {
		return UsuarioDAL.Listar(false);
	}
	
	public static List<UsuarioModel> Listar(Boolean somenteGestores) throws BDException {
		Connection conexao = Conexao.getConexao();
		try {
			List<UsuarioModel> pessoas = new ArrayList<UsuarioModel>();
			
			String query = "SELECT * FROM USUARIO WHERE 1=1";
			
			if (somenteGestores) {
				query += " AND perfil = ?";
			}
			
			PreparedStatement pst = conexao.prepareStatement(query);
			
			if (somenteGestores) {
				pst.setInt(1, EPerfil.GESTOR.getIndex());
			}
			
			ResultSet rs = pst.executeQuery();
			while (rs.next()) {
				pessoas.add(new UsuarioModel(
						rs.getInt("ID"), 
						rs.getString("NOME"), 
						rs.getString("EMAIL"), 
						rs.getString("SENHA"), 
						EPerfil.getEnum(rs.getInt("PERFIL")), 
						rs.getInt("COD_SETOR")));
			}
			return pessoas;
		} catch (Exception e) {
			throw new BDException(EErrosBD.CONSULTA, e.getMessage());
		} finally {
			Conexao.closeConexao();
		}
	}

	public static int GetQuantidadeAdministradores() throws BDException {
		Connection conexao = Conexao.getConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("SELECT COUNT(ID) as Qtd FROM USUARIO WHERE perfil = ?;");
			pst.setInt(1, EPerfil.ADMINISTRADOR.getIndex());
			ResultSet rs = pst.executeQuery();
			
			if (rs.first()) {
				return rs.getInt("Qtd");
			}
			
			return 0;
			
 		} catch (Exception e) {
 			throw new BDException(EErrosBD.CONSULTA, e.getMessage());
 		} finally {
 			Conexao.closeConexao();
 		}
	}

	public static Integer GetQuantidadeColaboradores(Integer id) throws BDException {
		Connection conexao = Conexao.getConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("SELECT COUNT(ID) as Qtd FROM USUARIO WHERE cod_setor = ?;");
			pst.setInt(1, id);
			ResultSet rs = pst.executeQuery();
			
			if (rs.first()) {
				return rs.getInt("Qtd");
			}
			
			return 0;
			
 		} catch (Exception e) {
 			throw new BDException(EErrosBD.CONSULTA, e.getMessage());
 		} finally {
 			Conexao.closeConexao();
 		}
	}

	public static String GetNome(Integer idGestor) throws BDException {
		Connection conexao = Conexao.getConexao();
		try {
			PreparedStatement pst = conexao.prepareStatement("SELECT nome FROM USUARIO WHERE id = ?;");
			pst.setInt(1, idGestor);
			ResultSet rs = pst.executeQuery();
			
			if (rs.first()) {
				return rs.getString("nome");
			}
			
			return null;
			
 		} catch (Exception e) 
		{
 			throw new BDException(EErrosBD.CONSULTA, e.getMessage());
 		} finally {
 			Conexao.closeConexao();
 		}
	}

}
