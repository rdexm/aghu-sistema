package br.gov.mec.aghu.exames.contratualizacao.business;

import java.text.ParseException;
import java.util.Map;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings("PMD.PackagePrivateSeamContextsManager")
public abstract class ContratualizacaoCommand extends BaseBusiness{
	private static final long serialVersionUID = -6890602087537497017L;
	public static final String PACIENTE_AGHU = "PACIENTE_AGHU";
	public static final String PACIENTE_INTEGRACAO = "PACIENTE_INTEGRACAO";
	public static final String MEDICO_AGHU = "MEDICO_EXTERNO_AGHU";
	public static final String MEDICO_INTEGRACAO = "MEDICO_SOLICITANTE_INTEGRACAO";
	public static final String HEADER_INTEGRACAO = "HEADER_INTEGRACAO";
	public static final String ATENDIMENTO_AGHU = "ATENDIMENTO_AGHU";
	public static final String SOLICITACAO_INTEGRACAO = "SOLICITACAO_INTEGRACAO";
	public static final String ITENS_SOLICITACAO_INTEGRACAO = "ITENS_SOLICITACAO_INTEGRACAO";
	public static final String NOME_MICROCOMPUTADOR = "NOME_MICROCOMPUTADOR";
	public static final String SERVIDOR_LOGADO = "SERVIDOR_LOGADO";
	abstract Map<String, Object> executar(Map<String, Object> parametros) throws NumberFormatException, BaseException, ParseException;
	abstract boolean comitar();
}
