package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.MbcSolicitacaoCirurgiaPosEscala;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * VO principal das estórias relacionadas ao agendamento e registro de cirurgias:
 * 
 * #22460 - Agendar procedimentos eletivo, urgência ou emergência
 * <p>
 * #24941 - Registro de cirurgia realizada e nota de consumo
 * 
 * @author aghu
 * 
 */
public class CirurgiaTelaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3510889928130042855L;

	/*
	 * Campos principais do FORMS e análogos a cirúrgia
	 */
	private MbcCirurgias cirurgia;

	/*
	 * Outros atributos (UTILIZADOS NAS PROCEDURES) TODO Mover PARA AS ONs se necessário!
	 */
	private DominioTipoPlano tipoPlano; // Tipo do plano (DSP_IND_TIPO_PLANO)
	private Integer prontuario; // Prontuário (DRV_CRIADO_EM)
	private AipPacientes pacienteAntigo; // Armazena o PACIENTE ANTERIOR quando ocorre edição (V_PAC_COD_ANT)
	private MbcSolicitacaoCirurgiaPosEscala solicitacaoCirurgiaPosEscala; // Recebe por parâmetro na controller (PARAMETER.P_SPE_SEQ)
	private Short tempoMaximoCirurgia; // DSP_TEMPO_MAXIMO_CIRURGIA

	/*
	 * GLOBAIS TODO Mover PARA AS ONs se necessário!
	 */
	private Boolean deveTerAnestesista; // GLOBAL.DEVE_TER_ANESTESISTA
	private Integer atcnsAtendimentoAnterior; // GLOBAL.MBC$ATCNS_ATENDIMENTO_ANTERIOR
	private Boolean indDigtNotaSala; // GLOBAL.MBC$IND_DIGT_NOTA_SALA

	/*
	 * Listagens
	 */

	/*
	 * Listas de procedimentos, profissionais e anestesias
	 */
	private List<CirurgiaTelaProcedimentoVO> listaProcedimentosVO = new ArrayList<CirurgiaTelaProcedimentoVO>();
	private List<CirurgiaTelaProfissionalVO> listaProfissionaisVO = new ArrayList<CirurgiaTelaProfissionalVO>();
	private List<CirurgiaTelaAnestesiaVO> listaAnestesiasVO = new ArrayList<CirurgiaTelaAnestesiaVO>();

	/*
	 * Listas de procedimentos, profissionais e anestesias REMOVIDOS
	 */
	private List<MbcProcEspPorCirurgiasId> listaProcedimentosRemovidos = new ArrayList<MbcProcEspPorCirurgiasId>();
	private List<MbcProfCirurgiasId> listaProfissionaisRemovidos = new ArrayList<MbcProfCirurgiasId>();
	private List<MbcAnestesiaCirurgiasId> listaAnestesiasRemovidas = new ArrayList<MbcAnestesiaCirurgiasId>();

	public enum CirurgiaTelaVOExceptionCode implements BusinessExceptionCode {PROCEDIMENTOS_DEVE_CONTER_PRINCIPAL, PROFISSIONAIS_DEVE_CONTER_RESPONSAVEL}
	
	public CirurgiaTelaVO() {
		/*
		 * ATENÇÃO: AO CONSTRUIR O VO A CIRURGIA SEMPRE DEVERÁ SER INSTÂNCIADA
		 */
		this.cirurgia = new MbcCirurgias();
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public DominioTipoPlano getTipoPlano() {
		return tipoPlano;
	}

	public void setTipoPlano(DominioTipoPlano tipoPlano) {
		this.tipoPlano = tipoPlano;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AipPacientes getPacienteAntigo() {
		return pacienteAntigo;
	}

	public void setPacienteAntigo(AipPacientes pacienteAntigo) {
		this.pacienteAntigo = pacienteAntigo;
	}

	public MbcSolicitacaoCirurgiaPosEscala getSolicitacaoCirurgiaPosEscala() {
		return solicitacaoCirurgiaPosEscala;
	}

	public void setSolicitacaoCirurgiaPosEscala(MbcSolicitacaoCirurgiaPosEscala solicitacaoCirurgiaPosEscala) {
		this.solicitacaoCirurgiaPosEscala = solicitacaoCirurgiaPosEscala;
	}

	public List<CirurgiaTelaProcedimentoVO> getListaProcedimentosVO() {
		return listaProcedimentosVO;
	}

	public void setListaProcedimentosVO(List<CirurgiaTelaProcedimentoVO> listaProcedimentosVO) {
		this.listaProcedimentosVO = listaProcedimentosVO;
	}

	public List<CirurgiaTelaProfissionalVO> getListaProfissionaisVO() {
		return listaProfissionaisVO;
	}

	public void setListaProfissionaisVO(List<CirurgiaTelaProfissionalVO> listaProfissionaisVO) {
		this.listaProfissionaisVO = listaProfissionaisVO;
	}

	public List<CirurgiaTelaAnestesiaVO> getListaAnestesiasVO() {
		return listaAnestesiasVO;
	}

	public void setListaAnestesiasVO(List<CirurgiaTelaAnestesiaVO> listaAnestesiasVO) {
		this.listaAnestesiasVO = listaAnestesiasVO;
	}

	public List<MbcProcEspPorCirurgiasId> getListaProcedimentosRemovidos() {
		return listaProcedimentosRemovidos;
	}

	public void setListaProcedimentosRemovidos(List<MbcProcEspPorCirurgiasId> listaProcedimentosRemovidos) {
		this.listaProcedimentosRemovidos = listaProcedimentosRemovidos;
	}

	public List<MbcProfCirurgiasId> getListaProfissionaisRemovidos() {
		return listaProfissionaisRemovidos;
	}

	public void setListaProfissionaisRemovidos(List<MbcProfCirurgiasId> listaProfissionaisRemovidos) {
		this.listaProfissionaisRemovidos = listaProfissionaisRemovidos;
	}

	public List<MbcAnestesiaCirurgiasId> getListaAnestesiasRemovidas() {
		return listaAnestesiasRemovidas;
	}

	public void setListaAnestesiasRemovidas(List<MbcAnestesiaCirurgiasId> listaAnestesiasRemovidas) {
		this.listaAnestesiasRemovidas = listaAnestesiasRemovidas;
	}

	public Short getTempoMaximoCirurgia() {
		return tempoMaximoCirurgia;
	}

	public void setTempoMaximoCirurgia(Short tempoMaximoCirurgia) {
		this.tempoMaximoCirurgia = tempoMaximoCirurgia;
	}

	public Integer getAtcnsAtendimentoAnterior() {
		return atcnsAtendimentoAnterior;
	}

	public void setAtcnsAtendimentoAnterior(Integer atcnsAtendimentoAnterior) {
		this.atcnsAtendimentoAnterior = atcnsAtendimentoAnterior;
	}

	public Boolean getIndDigtNotaSala() {
		return indDigtNotaSala;
	}

	public void setIndDigtNotaSala(Boolean indDigtNotaSala) {
		this.indDigtNotaSala = indDigtNotaSala;
	}

	public Boolean getDeveTerAnestesista() {
		return deveTerAnestesista;
	}

	public void setDeveTerAnestesista(Boolean deveTerAnestesista) {
		this.deveTerAnestesista = deveTerAnestesista;
	}

	/*
	 * Métodos utilitários do VO
	 */

	/**
	 * Obtém a data da truncada
	 */
	public final Date getDataCirurgiaTruncada() {
		return DateUtil.truncaData(this.cirurgia.getData());
	}

	/**
	 * Obtém o procedimento principal (MARCADO NA TELA)
	 * 
	 * @return
	 */
	public final CirurgiaTelaProcedimentoVO obterProcedimentoPrincipal() throws ApplicationBusinessException {
		for (CirurgiaTelaProcedimentoVO procedimentoPrincipal : this.listaProcedimentosVO) {
			if (Boolean.TRUE.equals(procedimentoPrincipal.getIndPrincipal())) {
				return procedimentoPrincipal;
			}
		}
		throw new ApplicationBusinessException(CirurgiaTelaVOExceptionCode.PROCEDIMENTOS_DEVE_CONTER_PRINCIPAL);
	}

	/**
	 * Obtém o profissional responsável (MARCADO NA TELA)
	 * 
	 * @return
	 */
	public final CirurgiaTelaProfissionalVO obterProfissionalResponsavel() throws ApplicationBusinessException {
		for (CirurgiaTelaProfissionalVO profissionalVO : this.listaProfissionaisVO) {
			if (Boolean.TRUE.equals(profissionalVO.getIndResponsavel())) {
				return profissionalVO;
			}
		}
		throw new ApplicationBusinessException(CirurgiaTelaVOExceptionCode.PROFISSIONAIS_DEVE_CONTER_RESPONSAVEL);
	}

	/**
	 * Obtém a descrição completa do motivo de cancelamento
	 * 
	 * @return
	 */
	public final String getDescricaoMotivoCancelamento() {
		if (this.getCirurgia().getMotivoCancelamento() != null) {
			return this.getCirurgia().getMotivoCancelamento().getSeq() + " - " + this.getCirurgia().getMotivoCancelamento().getDescricao();
		}
		return null;
	}

}
