package br.gov.mec.aghu.ambulatorio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacConsultasJn;
import br.gov.mec.aghu.model.AacMotivos;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ListarHistoricoConsultaController extends ActionController {

	private static final long serialVersionUID = -1889771887380504938L;

	private final String PESQUISAR_CONSULTAS_PACIENTE = "pesquisarConsultasPaciente";

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private AacConsultasJn selecionado;
	
	private AacConsultas consulta;
	
	private List<AacConsultasJn> historicoConsulta;
	
	private Integer numConsulta;
	
	private String cameFrom;
	private String nomeResponsavelMarcacao;
	

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

    public void iniciar() {
    	final Enum[] innerJoin = {AacConsultas.Fields.GRADE_AGENDA_CONSULTA, AacConsultas.Fields.GRADE_AGENDA_CONSULTA_ESPECIALIDADE};
    	final Enum[] leftJoin  = {AacConsultas.Fields.SITUACAO_CONSULTA, AacConsultas.Fields.CONSULTA, AacConsultas.Fields.PACIENTE,
    							  AacConsultas.Fields.RETORNO, AacConsultas.Fields.PROJETO_PESQUISA, AacConsultas.Fields.PAGADOR,
    							  AacConsultas.Fields.TIPO_AGENDAMENTO, AacConsultas.Fields.CONVENIO_SAUDE_PLANO, 
    							  AacConsultas.Fields.CONVENIO_SAUDE_PLANO_CV, AacConsultas.Fields.CONDICAO_ATENDIMENTO,
    							  AacConsultas.Fields.SERVIDOR, AacConsultas.Fields.SERVIDOR_PESSOA_FISICA};
    	
		consulta = this.ambulatorioFacade.obterConsultaPorNumero(numConsulta, innerJoin, leftJoin);
		
		nomeResponsavelMarcacao = ambulatorioFacade.obterNomeResponsavelMarcacaoConsulta(consulta.getNumero());
	}
	
    
    public String voltar(){
    	historicoConsulta =  null;
    	
    	if (cameFrom != null && cameFrom.equals("ambulatorio-pesquisarConsultasGrade")) {
    		cameFrom = null;
    		return "ambulatorio-pesquisarConsultasGrade";
    	}
    	
    	return PESQUISAR_CONSULTAS_PACIENTE ;
    }
    
	public List<AacConsultasJn> obterHistoricoConsultas() {
		if(historicoConsulta==null) {
			historicoConsulta =  this.ambulatorioFacade.obterHistoricoConsultasPorNumero(numConsulta);
		}
		return historicoConsulta;
	}

	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	public AacConsultas getConsulta() {
		return consulta;
	}

	public void setHistoricoConsulta(List<AacConsultasJn> historicoConsulta) {
		this.historicoConsulta = historicoConsulta;
	}

	public List<AacConsultasJn> getHistoricoConsulta() {
		return historicoConsulta;
	}
	
	public Integer getNumConsulta() {
		return numConsulta;
	}

	public void setNumConsulta(Integer numConsulta) {
		this.numConsulta = numConsulta;
	}
	
	public String obterNomePacientePorCodigo(Integer pacCodigo) {
		if (pacCodigo == null) {
			return "";
		}
		
		AipPacientes paciente = pacienteFacade.obterNomePacientePorCodigo(pacCodigo);
		if (paciente == null) {
			return "";
		}
		
		return paciente.getNome();
	}
	
	/**
     * Obtém a descrição do motivo da consulta pelo código.
     */
	public String obterDescricaoMotivoPorCodigo(Short mtoSeq) {
		AacMotivos motivo = ambulatorioFacade.obterDescricaoMotivoPorCodigo(mtoSeq);
		if(motivo==null){
			return "";
		}
		return motivo.getDescricao();
	}
	
	/**
     * Obtém a descrição do retorno da consulta pelo código.
     */
	public String obterDescricaoRetornoPorCodigo(Integer retSeq) {
		AacRetornos retorno = ambulatorioFacade.obterDescricaoRetornoPorCodigo(retSeq);
		if (retorno == null) {
			return StringUtils.EMPTY;
		}
		return retorno.getDescricao();
	}
	
	/**
     * Obtém o nome do responsável pela movimentação.
     */
	public String obterResponsavel(Integer serMatriculaAlterado, Short serVinCodigoAlterado) {
		
		RapServidores servidor = registroColaboradorFacade.obterServidor(new RapServidoresId(serMatriculaAlterado, serVinCodigoAlterado));
		
		if(servidor==null){
			return "";
		}
		return servidor.getPessoaFisica().getNome();
	}
	
	/**
     * Obtém a operação executada no banco.
     */
	public String obterOperacao(DominioOperacoesJournal operacao) {
		if(operacao.getCodigo()==0){
			return "Atualização";
		}
		if(operacao.getCodigo()==1){
			return "Deleção";
		}
		if(operacao.getCodigo()==2){
			return "Inserção";
		}
		return "";
	}
	
	/**
     * Obtém a descrição da condição de atendimento da consulta pelo código.
     */
	public String obterCondicaoAtendimentoPorCodigo(Short fagCaaSeq) {
		if(fagCaaSeq!=null) {
			AacCondicaoAtendimento condicaoAtendimento = ambulatorioFacade.obterCondicaoAtendimentoPorCodigo(fagCaaSeq);
			if(condicaoAtendimento==null){
				return "";
			}
			else {
				return condicaoAtendimento.getDescricao();
			}
		}
		return "";
	}
	
	/**
     * Obtém a descrição do tipo de agendamento da consulta pelo código.
     */
	public String obterTipoAgendamentoPorCodigo(Short fagTagSeq) {
		if(fagTagSeq!=null) {
			AacTipoAgendamento tipoAgendamento = ambulatorioFacade.obterTipoAgendamentoPorCodigo(fagTagSeq);
			if(tipoAgendamento==null) {
				return "";
			}
			return tipoAgendamento.getDescricao();
		}
		return "";
	}
	
	/**
     * Obtém a descrição do pagador da consulta pelo código.
     */
	public String obterPagadorPorCodigo(Short fagPgdSeq) {
		if(fagPgdSeq!=null) {
			AacPagador pagador = ambulatorioFacade.obterPagadorPorCodigo(fagPgdSeq);
			if(pagador==null) {
				return "";
			}
			return pagador.getDescricao();
		}
		return "";
	}
	
	/**
     * Obtém o número do prontuário pelo código do paciente.
     */
	public Integer obterProntuarioPaciente(Integer pacCodigo) {
		return pacienteFacade.obterProntuarioPorPacCodigo(pacCodigo);
	}

	public String getNomeResponsavelMarcacao() {
		return nomeResponsavelMarcacao;
	}

	public void setNomeResponsavelMarcacao(String nomeResponsavelMarcacao) {
		this.nomeResponsavelMarcacao = nomeResponsavelMarcacao;
	}

	public String getCameFrom() {
		return cameFrom;
	}


	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public AacConsultasJn getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AacConsultasJn selecionado) {
		this.selecionado = selecionado;
	}
	
}
