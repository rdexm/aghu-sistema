package br.gov.mec.aghu.exames.action;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
import javax.faces.validator.DoubleRangeValidator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristicaId;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;

/**
 * Controller da POC de máscara de exames
 */

public class DesenhoMascaraExamesController extends ActionController {

	private static final long serialVersionUID = 3996725996699904785L;
	private static final Log LOG = LogFactory.getLog(DesenhoMascaraExamesController.class);

	/**
	 * Constante para o nome do atributo de cada componente que armazenará a
	 * instância de AelParametroCamposLaudo correspondente.
	 */
	private static final String NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO = "PARAMETRO_CAMPO_LAUDO";

	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private MascaraExamesComponentes mascaraExamesComponentes;

	/**
	 * Parâmetro de entrada na tela. Representa o sequencial da solicitação de
	 * exames.
	 */
	private Integer solicitacaoExameSeq;

	/**
	 * Parâmetro de entrada na tela. Representa o sequencial do item da
	 * solicitação de exame.
	 */
	private Short itemSolicitacaoExameSeq;

	/**
	 * Parâmetro de entrada da tela utilizado para buscar a versão da <br>
	 * máscara do laudo a ser utilizado no item da solicitação do exame.
	 */
	private Integer velSeqp;
		
	private List<DesenhoMascaraExameVO> desenhosMascarasExamesVO;

	private boolean possuiResultados = false;
	
	private AelItemSolicitacaoExames itemSolicitacao;

	@PostConstruct
	public void init(){
	 this.begin(conversation);
	 inicializar();
	}	
	
	/**
	 * Método executado na carga da página. Carrega os dados da máscara a ser
	 * exibida e instancia os componentes de acordo.
	 * @throws BaseException 
	 */
	public void inicializar() {
		try {
			Boolean isHist = Boolean.FALSE;//Não foi encontrado necessidade desta classe possuir histórico por enquanto
			this.desenhosMascarasExamesVO = mascaraExamesComponentes.buscaDesenhosMascarasExamesVO(this.solicitacaoExameSeq, this.itemSolicitacaoExameSeq, this.velSeqp, isHist);
			if(this.desenhosMascarasExamesVO!= null && !this.desenhosMascarasExamesVO.isEmpty()){
				this.possuiResultados = this.desenhosMascarasExamesVO.get(0).isPossuiResultados(); 
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
			
		}
		
	}

	public void validarNormalidade() throws ApplicationBusinessException, BaseListException {
		Map<AelParametroCamposLaudo, Object> valoresCampos = new HashMap<AelParametroCamposLaudo, Object>();
		for (DesenhoMascaraExameVO desenhoMascaraExameVO : this.desenhosMascarasExamesVO) {
			this.preencherValores(desenhoMascaraExameVO.getFormularioDinamicoPanel().getChildren(), valoresCampos);
		}
	}

	protected void preencherValores(List<UIComponent> listaComponentes, Map<AelParametroCamposLaudo, Object> valoresCampos) throws ApplicationBusinessException {
		for (UIComponent componente : listaComponentes) {
			if (componente instanceof UIInput) {
				UIInput uiComponente = (UIInput) componente;
				Object value = null;

				if(uiComponente instanceof HtmlSelectOneMenu){
					if(uiComponente.getValue() != null && uiComponente.getValue().toString().indexOf("RESULTADO_CODIFICADO")>-1){
						String[] chaves = uiComponente.getValue().toString().split(":")[1].split(",");
						value = examesFacade.obterResultadoCodificadoPorId(new AelResultadoCodificadoId(Integer.parseInt(chaves[0].split("=")[1]), Integer.parseInt(chaves[1].split("=")[1])));

					}else if(uiComponente.getValue() != null && uiComponente.getValue().toString().indexOf("GRUPO_CARACTERISTICA")>-1){
						String[] chaves = uiComponente.getValue().toString().split(":")[1].split(",");

						AelExameGrupoCaracteristicaId id = new AelExameGrupoCaracteristicaId();
						id.setEmaExaSigla(chaves[0].split("=")[1]);
						id.setEmaManSeq(Integer.parseInt(chaves[1].split("=")[1]));
						id.setCacSeq(Integer.parseInt(chaves[2].split("=")[1]));
						id.setGcaSeq(Integer.parseInt(chaves[3].split("=")[1]));
						value = examesFacade.obterAelExameGrupoCaracteristicaPorId(id);						

					}else{
						value = uiComponente.getValue();	
					}
				}else{
					value = uiComponente.getValue();
				}
				
				AelParametroCamposLaudo parametroCampoLaudo = (AelParametroCamposLaudo) uiComponente.getAttributes().get(NOME_ATRIBUTO_PARAMETRO_CAMPO_LAUDO);
				List<DoubleRangeValidator> validators = null;
				if(parametroCampoLaudo != null){
					validators = mascaraExamesComponentes.obterListaValidatorsValoresNormalidade(parametroCampoLaudo, itemSolicitacao);
				}
				if (validators != null && !validators.isEmpty()) {
					Number retorno = null;
					NumberFormat format = NumberFormat.getInstance(new Locale(
							"pt", "BR"));
					if (value instanceof String) {
						try {
							retorno = format.parse((String) value);
						} catch (ParseException e) {
							LOG.error(e.getMessage());
						}
					} else {
						retorno = (Number) value;
					}
					for (DoubleRangeValidator doubleRangeValidator : validators) {
						try{
							doubleRangeValidator.validate(FacesContext.getCurrentInstance(), uiComponente, retorno);
						}catch (ValidatorException e) {
							apresentarMsgNegocio(Severity.WARN, e.getMessage());
						}
					}
				}

				if(value instanceof String){
					value = StringEscapeUtils.unescapeHtml4(value.toString());
				}
				if(parametroCampoLaudo != null){
					valoresCampos.put(parametroCampoLaudo, value);
					LOG.debug("Campo Laudo: " + parametroCampoLaudo.getId() + " - " + value);
				}
				
			}
			this.preencherValores(componente.getChildren(), valoresCampos);
		}
	}
	
	/**
	 * Reseta parâmetros de conversação da tela
	 */
	protected void limparParametros() {
		this.solicitacaoExameSeq = null;
		this.itemSolicitacaoExameSeq = null;
		this.velSeqp = null;
		this.desenhosMascarasExamesVO = null;
		this.possuiResultados = false;
		this.itemSolicitacao = null;
	}

	// GETERS E SETERS
	public Integer getSolicitacaoExameSeq() {
		return solicitacaoExameSeq;
	}

	public void setSolicitacaoExameSeq(Integer solicitacaoExameSeq) {
		this.solicitacaoExameSeq = solicitacaoExameSeq;
	}

	public Short getItemSolicitacaoExameSeq() {
		return itemSolicitacaoExameSeq;
	}

	public void setItemSolicitacaoExameSeq(Short itemSolicitacaoExameSeq) {
		this.itemSolicitacaoExameSeq = itemSolicitacaoExameSeq;
	}

	public List<DesenhoMascaraExameVO> getDesenhosMascarasExamesVO() {
		return desenhosMascarasExamesVO;
	}

	public void setDesenhosMascarasExamesVO(List<DesenhoMascaraExameVO> desenhosMascarasExamesVO) {
		this.desenhosMascarasExamesVO = desenhosMascarasExamesVO;
	}

	public Integer getVelSeqp() {
		return velSeqp;
	}

	public void setVelSeqp(Integer velSeqp) {
		this.velSeqp = velSeqp;
	}

	public boolean isPossuiResultados() {
		return possuiResultados;
	}

	public void setPossuiResultados(boolean possuiResultados) {
		this.possuiResultados = possuiResultados;
	}

	public AelItemSolicitacaoExames getItemSolicitacao() {
		return itemSolicitacao;
	}

	public void setItemSolicitacao(AelItemSolicitacaoExames itemSolicitacao) {
		this.itemSolicitacao = itemSolicitacao;
	}

	public MascaraExamesComponentes getMascaraExamesComponentes() {
		return mascaraExamesComponentes;
	}

}
