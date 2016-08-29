package br.gov.mec.aghu.exames.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelVersaoLaudo;
import br.gov.mec.aghu.model.AelVersaoLaudoId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class CadastroResultadoPadraoLaudoController extends DesenhoMascaraExamesController {


	private static final long serialVersionUID = 9071134885675323928L;

	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private CalculaValorMascaraExamesComponente calculaValorMascaraExamesController;
	
	@Inject
	private MascaraExamesComponentes mascaraExamesComponentes;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// Parâmetros de conversação
	private Integer seqResultadosPadrao; // Seq do resultado padrão
	private String velEmaExaSigla; // Sigla do exame da versão do laudo
	private Integer velEmaManSeq; // Material da versão do laudo
	private Integer velSeqp; // Seqpda versão do laudo
	private Integer alturaDiv;
	
	private Boolean emEdicao; // Controle de edição
	private String voltarPara; // Controla a navegação do botão voltar
	
	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Chamado no inicio de cada conversação
	 * 
	 * @return
	 */
	public String iniciar() {
	 
		try{
			// Obtém a versão do laudo
			this.versaoLaudo = this.examesFacade.obterVersaoLaudoComDependencias(this.obterIdVersaoLaudo());
	
			if (emEdicao) { // Alterando
				this.resultadoPadrao = this.examesFacade.obterResultadosPadraoPorSeq(this.seqResultadosPadrao);
			} else { // Inserindo
				this.resultadoPadrao = new AelResultadosPadrao();
				this.resultadoPadrao.setSituacao(DominioSituacao.A); // Valor padrão é ativo
				// Seta exame material de análise
				this.resultadoPadrao.setExameMaterialAnalise(versaoLaudo.getExameMaterialAnalise());
			}
	
			// Integração com a POC
			if(emEdicao){
				this.setDesenhosMascarasExamesVO(this.mascaraExamesComponentes.montaDesenhosMascarasExamesResultadoPadrao(this.resultadoPadrao, this.getVelSeqp(), null, null));
			} else{
				this.setDesenhosMascarasExamesVO(this.mascaraExamesComponentes.buscarDesenhosMascarasExamesResultadoPadraoVO(this.versaoLaudo));
				
			}
	
			calculaValorMascaraExamesController.setPrevia(false);
			
		} catch (BaseException be){
			apresentarExcecaoNegocio(be);
		}
		return null;
	
	}

	/**
	 * Obtém o id da versão do laudo
	 * 
	 * @return
	 */
	public AelVersaoLaudoId obterIdVersaoLaudo() {

		AelVersaoLaudoId id = new AelVersaoLaudoId();
		id.setEmaExaSigla(this.velEmaExaSigla);
		id.setEmaManSeq(this.velEmaManSeq);
		id.setSeqp(this.velSeqp);
		return id;
	}

	/**
	 * Grava resultados padrão de laudo
	 * 
	 * @return
	 */
	public String gravarResultadoPadraoCampoLaudo() {
		try {
			// Persiste resultado padrão
			this.cadastrosApoioExamesFacade.persistirResultadoPadrao(this.resultadoPadrao);
			
			Map<AelParametroCamposLaudo, Object> mapaParametroCamposLaudoTelaValores = new HashMap<AelParametroCamposLaudo, Object>();
			for (DesenhoMascaraExameVO desenhoMascaraExameVO : super.getDesenhosMascarasExamesVO()) {
				this.preencherValores(desenhoMascaraExameVO.getFormularioDinamicoPanel().getChildren(), mapaParametroCamposLaudoTelaValores);
			}
			
			// Persiste resultado padrão do campo laudo
			this.examesFacade.gravarResultadoPadraoCampoLaudo(this.versaoLaudo, this.resultadoPadrao, mapaParametroCamposLaudoTelaValores);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVACAO_RESULTADO_PADRAO_LAUDO");

			return this.voltarPara;
			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public Object calcularValor(String idCampo){
		try{
			calculaValorMascaraExamesController.setDesenhosMascarasExamesVO(this.getDesenhosMascarasExamesVO());
			return calculaValorMascaraExamesController.calcularValor(idCampo);
		}catch(Exception e){
			apresentarMsgNegocio(Severity.ERROR, "MSG_EXCESSAO_EXPRESSAO");
		}
		return null;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		return this.voltarPara;
	}
	
	public List<DesenhoMascaraExameVO> montaDesenhosMascarasExamesResultadoPadrao(final AelResultadosPadrao resultadoPadrao,
			final Integer velSeqp, final Integer solicitacaoExameSeq, final Short itemSolicitacaoExameSeq) throws BaseException{
		
		List<DesenhoMascaraExameVO> listaDesenhoMascaraExameVO = this.mascaraExamesComponentes.montaDesenhosMascarasExamesResultadoPadrao(resultadoPadrao, velSeqp, solicitacaoExameSeq, itemSolicitacaoExameSeq); 
		
		this.setDesenhosMascarasExamesVO(listaDesenhoMascaraExameVO);
		
		return listaDesenhoMascaraExameVO;
	}
	
	/**
	 * Parâmetros
	 */
	
	/**
	 * Parâmetro de entrada na tela. Representa a versão do laudo do campo
	 */
	protected AelVersaoLaudo versaoLaudo;
	
	/**
	 * Parâmetro de entrada na tela. Representa o resultado padrão do campo
	 */
	protected AelResultadosPadrao resultadoPadrao;


	public AelVersaoLaudo getVersaoLaudo() {
		return this.versaoLaudo;
	}

	public void setVersaoLaudo(AelVersaoLaudo versaoLaudo) {
		this.versaoLaudo = versaoLaudo;
	}

	public AelResultadosPadrao getResultadoPadrao() {
		return this.resultadoPadrao;
	}

	public void setResultadoPadrao(AelResultadosPadrao resultadoPadrao) {
		this.resultadoPadrao = resultadoPadrao;
	}

	public Integer getSeqResultadosPadrao() {
		return seqResultadosPadrao;
	}

	public void setSeqResultadosPadrao(Integer seqResultadosPadrao) {
		this.seqResultadosPadrao = seqResultadosPadrao;
	}

	public String getVelEmaExaSigla() {
		return velEmaExaSigla;
	}

	public void setVelEmaExaSigla(String velEmaExaSigla) {
		this.velEmaExaSigla = velEmaExaSigla;
	}

	public Integer getVelEmaManSeq() {
		return velEmaManSeq;
	}

	public void setVelEmaManSeq(Integer velEmaManSeq) {
		this.velEmaManSeq = velEmaManSeq;
	}

	public Integer getVelSeqp() {
		return velSeqp;
	}

	public void setVelSeqp(Integer velSeqp) {
		this.velSeqp = velSeqp;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getAlturaDiv() {
		return alturaDiv;
	}

	public void setAlturaDiv(Integer alturaDiv) {
		this.alturaDiv = alturaDiv;
	}

}