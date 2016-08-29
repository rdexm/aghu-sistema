package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFormaCalculoAprazamento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAprazamentoFrequencia;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTipoFrequenciaAprazamentoController extends ActionController  {
	private static final long serialVersionUID = -5859654119680287136L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;	

	private MpmTipoFrequenciaAprazamento entity;
	private MpmAprazamentoFrequencia detail;
	private List<MpmAprazamentoFrequencia> aprazamentoFrequenciaList;
	private List<MpmAprazamentoFrequencia> aprazamentoFrequenciaExcluidosList;
	
	private boolean formularioEditado;

	private static final String REDIRECIONA_EDICAO = "manterTipoFrequenciaAprazamento";
	private static final String REDIRECIONA_LISTA = "listarTipoFrequenciaAprazamento";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String novo() {
		entity=new MpmTipoFrequenciaAprazamento();
		entity.setIndDigitaFrequencia(true);
		entity.setIndUsoHemoterapia(false);
		entity.setIndUsoQuimioterapia(false);
		entity.setIndSituacao(DominioSituacao.A);
		
		aprazamentoFrequenciaList = new ArrayList<MpmAprazamentoFrequencia>();
		aprazamentoFrequenciaExcluidosList=new ArrayList<MpmAprazamentoFrequencia>();
		
		formularioEditado=false;
		
		instanceDetail();
		
		return REDIRECIONA_EDICAO;
	}	

	private void instanceDetail(){
		detail=new MpmAprazamentoFrequencia();
		detail.setSituacao(DominioSituacao.A);
	}

	
	public String edit(MpmTipoFrequenciaAprazamento element) {
		entity=element;	
		detail = new MpmAprazamentoFrequencia();
		aprazamentoFrequenciaList = prescricaoMedicaFacade.listarAprazamentosFrequenciaPorTipo(element);
		aprazamentoFrequenciaExcluidosList=new ArrayList<MpmAprazamentoFrequencia>();
		formularioEditado=false;
		return REDIRECIONA_EDICAO;
	}
	
	public String salvar() {
		try {
			this.prescricaoMedicaFacade.salvarTipoFrequenciaAprazamento(entity, aprazamentoFrequenciaList, aprazamentoFrequenciaExcluidosList);
			aprazamentoFrequenciaExcluidosList=new ArrayList<MpmAprazamentoFrequencia>();
			this.apresentarMsgNegocio(Severity.INFO,"MSG_TIPO_FREQUENCIA_APRAZAMENTO_SALVO");
			
			entity=null;
			formularioEditado=false;
			return REDIRECIONA_LISTA;
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}			
		return null;
	}
	
	public void ativarInativar(){
		final DominioSituacao situacao = DominioSituacao.A.equals(detail.getSituacao()) ?  DominioSituacao.I : DominioSituacao.A;
		detail.setSituacao(situacao);
		formularioEditado = true;
	}
	
	
	public void excluirFrequenciaAprazamento() {
		try {
			if (entity.getSeq()!=null){
				prescricaoMedicaFacade.validarExclusaoTipoFrequenciaAprazamento(entity, false);
			}
			aprazamentoFrequenciaList.remove(detail);
			if (detail.getId()!=null){
				aprazamentoFrequenciaExcluidosList.add(detail);
			}	
			this.apresentarMsgNegocio(Severity.INFO,"MSG_TIPO_FREQUENCIA_APRAZAMENTO_EXCLUSAO2");		
			formularioEditado = true;						
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		detail = new MpmAprazamentoFrequencia();
	}	
	

	public void adicionarFrequencia(){
		
		try {
			prescricaoMedicaFacade.criarAprazamentoFrequencia(this.detail, this.entity);
		} catch ( ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		aprazamentoFrequenciaList.add(detail);
		instanceDetail();
		formularioEditado=true;
	}
	
	public String cancelarEdicao() {
		formularioEditado=false;
		instanceDetail();
		aprazamentoFrequenciaExcluidosList=new ArrayList<MpmAprazamentoFrequencia>();
		return REDIRECIONA_LISTA;
	}
	
	public void verificaDigitaFrequencia(){
		if (!entity.getIndDigitaFrequencia()){
			entity.setSintaxe(null);
		}
	}
	
	public DominioFormaCalculoAprazamento[] getItensForma(){
		return DominioFormaCalculoAprazamento.values();
	}


	public MpmTipoFrequenciaAprazamento getEntity() {
		return entity;
	}


	public void setEntity(MpmTipoFrequenciaAprazamento entity) {
		this.entity = entity;
	}

	public MpmAprazamentoFrequencia getDetail() {
		return detail;
	}

	public void setDetail(MpmAprazamentoFrequencia detail) {
		this.detail = detail;
	}

	public List<MpmAprazamentoFrequencia> getAprazamentoFrequenciaList() {
		return aprazamentoFrequenciaList;
	}

	public void setAprazamentoFrequenciaList(
			List<MpmAprazamentoFrequencia> aprazamentoFrequenciaList) {
		this.aprazamentoFrequenciaList = aprazamentoFrequenciaList;
	}

	public List<MpmAprazamentoFrequencia> getAprazamentoFrequenciaExcluidosList() {
		return aprazamentoFrequenciaExcluidosList;
	}

	public void setAprazamentoFrequenciaExcluidosList(
			List<MpmAprazamentoFrequencia> aprazamentoFrequenciaExcluidosList) {
		this.aprazamentoFrequenciaExcluidosList = aprazamentoFrequenciaExcluidosList;
	}
	
	public boolean editaTipoFrequenciaAprazamento() {
		try {
			return Boolean.valueOf(parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_DESABILITA_EDICAO_APRAZAMENTO).getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return false;
		} 
	} 

	public boolean isFormularioEditado() {
		return formularioEditado;
	}

	public void setFormularioEditado(boolean formularioEditado) {
		this.formularioEditado = formularioEditado;
	}
}