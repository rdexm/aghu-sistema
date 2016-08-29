package br.gov.mec.aghu.blococirurgico.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.DemoFinanceiroOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialHospitalarVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class DemoFinanceiroOPMEController extends ActionController implements ActionPaginator{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8020015164905129124L;


	/**
	 * 
	 */
	


	private static final Log LOG = LogFactory.getLog(DemoFinanceiroOPMEController.class);

	
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	private Date dtCompetencia;
	private Date dtCompetenciaInicial;
	private Date dtCompetenciaFinal;
	
	private AghEspecialidades especialidade;
	
	private MaterialHospitalarVO materialHospitalar;
	
	private Integer prontuario;
	
	private String voltarParaUrl;
	
	private Boolean pesquisar = Boolean.TRUE;
	
	private List<DemoFinanceiroOPMEVO> demoFinanceiroOPME;
	
	private Double totalCompativel;
	private Double totalIncompativel;
	
	private Boolean consolidarProntuario = Boolean.FALSE;
	
	
	@Inject @Paginator
	private DynamicDataModel<DemoFinanceiroOPMEVO> dataModel;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	public void iniciar() {
		//método para preparar tela
	
	}
	
	
	public void pesquisar() {
		//competência é valor obrigatório validado na tela
		
		if (dtCompetencia != null) {

			calculaDatasCompetencia();
			
			Integer materialHospParam = 0;
			if (materialHospitalar != null) {
				if (" TODOS ".equals(materialHospitalar.getMatNome())) {
					materialHospParam = -1;
				}else{
					materialHospParam = materialHospitalar.getMatCodigo();
				}
			}
			Integer prontuarioParam = this.prontuario;
			if (!consolidarProntuario && prontuarioParam == null) {
				prontuarioParam = -1;
			}

			try {
				this.demoFinanceiroOPME = blocoCirurgicoOpmesFacade
						.pesquisaDemonstrativoFinanceiroOpmes(
								this.dtCompetenciaInicial,
								this.dtCompetenciaFinal,
								especialidade != null ? especialidade.getSeq()
										: null,
								prontuarioParam,materialHospParam);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				LOG.error("Excecao Capturada: ", e);
			}
			if (this.demoFinanceiroOPME != null
					&& this.demoFinanceiroOPME.size() > 0) {
				this.demoFinanceiroOPME.add(createSummary());
			}
			pesquisar = Boolean.TRUE;

			this.dataModel.setPesquisaAtiva(true);
			this.dataModel.setWrappedData(this.demoFinanceiroOPME);

		}
	}
	
	/**
	 * Totais das colunas valores compatível e incompatível
	 * @return
	 */
	private DemoFinanceiroOPMEVO createSummary() {
		calculaValoresTotais();
		DemoFinanceiroOPMEVO vo = new DemoFinanceiroOPMEVO();
		vo.setMaterialHospitalar("Totais:");
		vo.setValorCompativel(totalCompativel);
		vo.setValorIncompativel(totalIncompativel);
		return vo;
	}
	private void calculaValoresTotais() {
		this.totalCompativel = 0d;
		this.totalIncompativel = 0d;
		for (DemoFinanceiroOPMEVO vo : this.demoFinanceiroOPME) {
			totalCompativel = totalCompativel + vo.getValorCompativel();
			totalIncompativel = totalIncompativel + vo.getValorIncompativel();
		}

	}
	private void calculaDatasCompetencia() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtCompetencia);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMinimum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, cal.getMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMinimum(Calendar.SECOND));
		this.dtCompetenciaInicial = cal.getTime();
		
		cal.setTime(dtCompetencia);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
		cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getMaximum(Calendar.SECOND));
		this.dtCompetenciaFinal = cal.getTime();
		
	}
	public void limpar() {
		this.dtCompetencia = null;
		this.especialidade = null;
		this.prontuario = null;
		this.materialHospitalar = null;
		this.demoFinanceiroOPME = null;
		this.dataModel.setPesquisaAtiva(Boolean.FALSE);
		this.dtCompetenciaInicial = null;
		this.dtCompetenciaFinal = null;
	}

	public String voltar() {
		return voltarParaUrl;
	}
	
	/**
	 * Sugestion especialidades
	 * @param param
	 * @return
	 */
	public List<AghEspecialidades> listarEspecialidades(String param) {

		List<AghEspecialidades> result = new ArrayList<AghEspecialidades>();
		AghEspecialidades todos = new AghEspecialidades();
		todos.setSeq((short) -1);
		todos.setNomeEspecialidade(" TODOS ");
		result.add(todos);
		result.addAll(this.aghuFacade
				.pesquisarEspecialidadesSemEspSeq((String) param));
		Long qtdResult = listarEspecialidadesCount(param);
		
		return this.returnSGWithCount(result, qtdResult);
	}
	public Long listarEspecialidadesCount(String param) {
		return this.aghuFacade.pesquisarEspecialidadesSemEspSeqCount((String) param);
	}
	
	/**
	 * Suggestion materialHospitalar
	 * @param matNome
	 * @return
	 */
	public List<MaterialHospitalarVO> pesquisarMaterialHospitalar(String matNome) {
		List<MaterialHospitalarVO> result = new ArrayList<MaterialHospitalarVO>();
		try {
			MaterialHospitalarVO mat = new MaterialHospitalarVO();
			mat.setMatNome(" TODOS ");
			result.add(mat);
			
			result.addAll(this.blocoCirurgicoOpmesFacade
					.pesquisarMaterialHospitalar(matNome));
			
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Excecao Capturada: ", e);
		}
		return result;
	}
	
	@Override
	public Long recuperarCount() {
		Long count = 0l;
		if(this.demoFinanceiroOPME != null){
			Integer size = this.demoFinanceiroOPME.size();
			count = size.longValue();
		}
		return count;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<DemoFinanceiroOPMEVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		
		if (pesquisar) {
			firstResult = 0;
			this.pesquisar = Boolean.FALSE;
		}
		int lastIndex = demoFinanceiroOPME.size();
		int lastPosition = 0;
		if((firstResult+maxResults)>lastIndex){
			lastPosition = lastIndex;
		}else{
			lastPosition = demoFinanceiroOPME.size();
		}
		
		
		return demoFinanceiroOPME.subList(firstResult,lastPosition);
	}
	
	//getters and setters

	public List<DemoFinanceiroOPMEVO> getDemoFinanceiroOPME() {
		return demoFinanceiroOPME;
	}

	public void setDemoFinanceiroOPME(
			List<DemoFinanceiroOPMEVO> demoFinanceiroOPME) {
		this.demoFinanceiroOPME = demoFinanceiroOPME;
	}

	public IBlocoCirurgicoOpmesFacade getBlocoCirurgicoOpmesFacade() {
		return blocoCirurgicoOpmesFacade;
	}

	public void setBlocoCirurgicoOpmesFacade(
			IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade) {
		this.blocoCirurgicoOpmesFacade = blocoCirurgicoOpmesFacade;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Boolean getPesquisar() {
		return pesquisar;
	}

	public void setPesquisar(Boolean pesquisar) {
		this.pesquisar = pesquisar;
	}

	public Date getDtCompetencia() {
		return dtCompetencia;
	}

	public Date getDtCompetenciaInicial() {
		return dtCompetenciaInicial;
	}
	public void setDtCompetenciaInicial(Date dtCompetenciaInicial) {
		this.dtCompetenciaInicial = dtCompetenciaInicial;
	}
	public Date getDtCompetenciaFinal() {
		return dtCompetenciaFinal;
	}
	public void setDtCompetenciaFinal(Date dtCompetenciaFinal) {
		this.dtCompetenciaFinal = dtCompetenciaFinal;
	}
	public void setDtCompetencia(Date dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
	public MaterialHospitalarVO getMaterialHospitalar() {
		return materialHospitalar;
	}
	public void setMaterialHospitalar(MaterialHospitalarVO materialHospitalar) {
		this.materialHospitalar = materialHospitalar;
	}
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public DynamicDataModel<DemoFinanceiroOPMEVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<DemoFinanceiroOPMEVO> dataModel) {
		this.dataModel = dataModel;
	}
	public Double getTotalCompativel() {
		return totalCompativel;
	}
	public void setTotalCompativel(Double totalCompativel) {
		this.totalCompativel = totalCompativel;
	}
	public Double getTotalIncompativel() {
		return totalIncompativel;
	}
	public void setTotalIncompativel(Double totalIncompativel) {
		this.totalIncompativel = totalIncompativel;
	}
	public Boolean getConsolidarProntuario() {
		return consolidarProntuario;
	}
	public void setConsolidarProntuario(Boolean consolidarProntuario) {
		this.consolidarProntuario = consolidarProntuario;
	}
	
}
