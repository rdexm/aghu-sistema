package br.gov.mec.aghu.ambulatorio.action;

import java.text.MessageFormat;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoUnidadeFuncionalSala;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AacUnidFuncionalSalasId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterZonasSalasController extends ActionController {

	private static final long serialVersionUID = -609718870995888018L;
	private static final String MANTER_ZONAS_SALAS_LIST = "manterZonasSalasList";	

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
    @Inject @Paginator("br.gov.mec.aghu.ambulatorio.action.ManterZonasSalasPaginatorController")
	private DynamicDataModel<AacUnidFuncionalSalas> dataModel;
	
	//Parametros
	private Short unfSeq;
	private Byte sala;
	
	//Insert e update
	private AacUnidFuncionalSalas zonasSalas;
	private AacUnidFuncionalSalas zonasSalasClone;
	
	private DominioOperacoesJournal operacao;

	// Labels parametrizados
	private String labelZona;
	private String labelSala;
	private String titleZona;
	private String titleSala;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	public enum ManterZonasSalasControllerExceptionCode implements
	BusinessExceptionCode {
		MSG_SALA_POR_UNIDADE_FUNCIONAL_GRAVADO_SUCESSO,
	}

	public void inicio() {
	 

	 

		if(zonasSalas != null){
			try {
				zonasSalasClone = (AacUnidFuncionalSalas) BeanUtils.cloneBean(zonasSalas);
			} catch (Exception e) {
				this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			}
			operacao = DominioOperacoesJournal.UPD;
		}
		else { //insert
			zonasSalas = new AacUnidFuncionalSalas();
			zonasSalas.setId(new AacUnidFuncionalSalasId());
			zonasSalas.setIndExcluido(false);
			zonasSalas.setSituacao(DominioSituacao.A);
			zonasSalas.setTipo(DominioTipoUnidadeFuncionalSala.C);
			operacao = DominioOperacoesJournal.INS;
		}
		
		try {
			labelZona = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
			labelSala = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
			
			String message = WebUtil.initLocalizedMessage("TITLE_ZONA_GRADE_AGENDAMENTO", null);
			this.titleZona = MessageFormat.format(message, this.labelZona);
			message = WebUtil.initLocalizedMessage("TITLE_PESQUISAR_PACIENTES_AGENDADOS_SALA", null);
			this.titleSala = MessageFormat.format(message, this.labelSala);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
	
	}
	
	
	public String gravar() {
		try {			
			zonasSalas.getId().setUnfSeq(zonasSalas.getUnidadeFuncional().getSeq());
			ambulatorioFacade.persistirZonaSala(zonasSalasClone, zonasSalas, operacao);
			this.dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, 
					ManterZonasSalasControllerExceptionCode.
					MSG_SALA_POR_UNIDADE_FUNCIONAL_GRAVADO_SUCESSO.toString(), this.labelSala);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		unfSeq = null;
		sala = null;
		zonasSalas = null;
		return MANTER_ZONAS_SALAS_LIST;		
	}

	public String cancelar() {
		unfSeq = null;
		sala = null;
		zonasSalas = null;
		return MANTER_ZONAS_SALAS_LIST;		
	}

	/** Suggestions de unidade funcional (zona/sala)
	 * 
	 * @param param
	 * @return
	 */
	public List<AghUnidadesFuncionais> listaUnidadeFuncionalComSiglaNaoNulla(String param) {
		return this.aghuFacade.listaUnidadeFuncionalComSiglaNaoNulla(param, DominioSituacao.A, 100, AghUnidadesFuncionais.Fields.SIGLA.toString());
	}

	public Long pesquisarPagadoresCount(Object param) {
		return this.aghuFacade.listaUnidadeFuncionalComSiglaNaoNullaCount(param, DominioSituacao.A);
	}
	
	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Byte getSala() {
		return sala;
	}

	public void setSala(Byte sala) {
		this.sala = sala;
	}

	public AacUnidFuncionalSalas getZonasSalas() {
		return zonasSalas;
	}

	public void setZonasSalas(AacUnidFuncionalSalas zonasSalas) {
		this.zonasSalas = zonasSalas;
	}

	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public String getLabelSala() {
		return labelSala;
	}

	public void setLabelSala(String labelSala) {
		this.labelSala = labelSala;
	}

	public AacUnidFuncionalSalas getZonasSalasClone() {
		return zonasSalasClone;
	}

	public void setZonasSalasClone(AacUnidFuncionalSalas zonasSalasClone) {
		this.zonasSalasClone = zonasSalasClone;
	}

	public DominioOperacoesJournal getOperacao() {
		return operacao;
	}

	public void setOperacao(DominioOperacoesJournal operacao) {
		this.operacao = operacao;
	}

	public String getTitleZona() {
		return titleZona;
	}

	public void setTitleZona(String titleZona) {
		this.titleZona = titleZona;
	}

	public String getTitleSala() {
		return titleSala;
	}

	public void setTitleSala(String titleSala) {
		this.titleSala = titleSala;
	}
}
