package br.gov.mec.aghu.dominio;

import java.util.HashMap;
import java.util.Map;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioOcorrenciaPOL implements Dominio{
	LIBERADO(1),
	LIBERADO_PRONTUARIO(2),
	LIBERADO_PROJETO(3),
	LIBERADO_ADMINISTRATIVO(4),
	LIBERADO_LISTA_ESPERA(5),
	LIBERADO_LISTA_CANCELADOS(6),
	LIBERADO_VIP(7),
	BLOQUEADO_VIP(51),
	BLOQUEADO_SEM_ATENDIMENTO(52),
	BLOQUEADO_SEM_PERFIL(53),
	BLOQUEADO_PRONTUARIO(54),
	OUTROS_BLOQUEIOS(99);
	
	private int codigo;
	
	DominioOcorrenciaPOL(int codigo){
		this.codigo = codigo;
	}
	private static final Map<Integer, DominioOcorrenciaPOL> typesByValue = new HashMap<Integer, DominioOcorrenciaPOL>();
	
	static {
		for(DominioOcorrenciaPOL type : DominioOcorrenciaPOL.values()){
			typesByValue.put(type.codigo, type);
		}
	}
	
	public static DominioOcorrenciaPOL forValue(int value){
		return typesByValue.get(value);
	}
	
	private static final Map<Integer, String> mapDescricao = new HashMap<Integer, String>();
	
	static{
		mapDescricao.put(1, "Acesso liberado");
		mapDescricao.put(2, "Acesso liberado para funcionário ver o seu prontuário ou de dependente");
		mapDescricao.put(3, "Acesso liberado para projeto de pesquisa");
		mapDescricao.put(4, "Acesso liberado para usuário administrativo");
		mapDescricao.put(5, "Acesso liberado para paciente da lista de espera do portal de agendamento de cirurgias");
		mapDescricao.put(6, "Acesso liberado para paciente da lista de cancelados do portal de agendamento de cirurgias");
		mapDescricao.put(7, "Acesso liberado para paciente VIP");
		mapDescricao.put(51, "Acesso bloqueado porque o paciente é VIP");
		mapDescricao.put(52, "Acesso bloqueado porque o paciente não tem atendimento");
		mapDescricao.put(53, "Acesso bloqueado porque o usuário não tem perfil");
		mapDescricao.put(54, "Acesso bloqueado porque usuário tentou ver prontuário que não é seu ou de depedente");
		mapDescricao.put(99, "Outros bloqueios");
	}
	

	@Override
	public int getCodigo() {
		return this.codigo;
	}

	@Override
	public String getDescricao() {
		return mapDescricao.get(this.codigo);
	}
	
	
	
}
