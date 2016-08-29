package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterContaHospitalarPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 137689572930731190L;

	private static final String PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject @Paginator
	private DynamicDataModel<VFatContaHospitalarPac> dataModel;
	private final String PAGE_MANTER_CONTA_HOSPITALAR = "manterContaHospitalar";

	
	// FILTRO
	private Integer contaHospitalar;
	private DominioSituacaoConta situacao;
	private VFatContaHospitalarPac fatContaHospitalarPacSelecionado;
	
	private String retorno;//Utilizada para controlar a execucao do metodo inicio para executar qdo vier da fonetica

	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	private AipPacientes paciente;
	
	@PostConstruct
	public void init() {		
		begin(conversation);
	}

	public void inicio() {
	 
		processaBuscaPaciente();
		/*if(codigo != null && this.retorno != null) {
			
			this.limparPesquisa();
			
			AipPacientes pac = pacienteFacade.obterPacientePorCodigo(filtro);
			this.nomePaciente = pac.getNome();
			this.codigo = filtro;
			this.prontuario = pac.getProntuario();
			
			this.retorno = null;
		}*/
	
	}
	
	public void pesquisar() {
		if(paciente == null && contaHospitalar == null) {
			apresentarMsgNegocio(Severity.ERROR, "CONTA_HOSPITALAR_PRONTUARIO_CODIGO_OBRIGATORIO");
		}
		else {
			obterPacientePorContaHospitalar();
			this.dataModel.reiniciarPaginator();
		}
	}

	
	/*public void obterPacientePorCodigo(){
		final Integer filtro = this.codigo;
		this.limparPesquisa();
	
		if(filtro != null){
			AipPacientes pac = pacienteFacade.obterPacientePorCodigo(filtro);
			if(pac != null){
				this.prontuario = pac.getProntuario();
				this.codigo = filtro;
				this.nomePaciente = pac.getNome();
			} else {
				apresentarMsgNegocio("FAT_00731");
			}
		}
	}

	public void obterPacientePorProntuario(){
		final Integer filtro = this.prontuario;
		this.limparPesquisa();
		
		if(filtro != null){
			AipPacientes pac = pacienteFacade.obterPacientePorProntuario(filtro);
			if(pac != null){
				this.codigo =pac.getCodigo();
				this.prontuario = filtro;
				this.nomePaciente = pac.getNome();
			} else {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO");
			}
		}
	}*/
	
	public void obterPacientePorContaHospitalar(){
		if (contaHospitalar != null) {
			final VFatContaHospitalarPac paciente = faturamentoFacade
					.obterVFatContaHospitalarPac(contaHospitalar);
			if (paciente != null) {
				/*nomePaciente = paciente.getPacNome();
				codigo = paciente.getPacCodigo();
				prontuario = paciente.getPacProntuario();*/
				this.paciente = paciente.getPaciente(); 
			}
		}
	}
	
	public void limparPesquisa() {
	
		this.contaHospitalar = null;
		this.paciente = null;
		this.situacao = null;
		
		this.dataModel.limparPesquisa();
	}
	
	public String editar() {
		return PAGE_MANTER_CONTA_HOSPITALAR;
	}
	
	@Override
	public Long recuperarCount() {		
		return this.faturamentoFacade.pesquisarAbertaOuFechadaCount(
								paciente != null ? paciente.getProntuario() : null,
								contaHospitalar, 
								paciente != null ? paciente.getCodigo() : null,
								situacao);
	}

	@Override
	public List<VFatContaHospitalarPac> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		List<VFatContaHospitalarPac> lista = new ArrayList<VFatContaHospitalarPac>();
		
		lista = this.faturamentoFacade.pesquisarAbertaOuFechadaOrdenadaPorDtIntAdm(firstResult, 
				maxResult, orderProperty, asc, 
				paciente != null ? paciente.getProntuario() : null, 
				contaHospitalar, 
				paciente != null ? paciente.getCodigo() : null,
				situacao);
		
		//55069
		if(lista != null && !lista.isEmpty()){
			if(faturamentoFacade.isPacienteTransplantado(paciente)){
				// ATENCÃO. Paciente é transplantado!
				apresentarMsgNegocio(Severity.INFO,"MBC_00537", paciente.getNome());
			}
		}
		
		return lista;
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void processaBuscaPaciente(){
		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) { 
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
			}
		}
	}
	
	public String redirecionarPesquisaFonetica() {
		paciente = null;
		return PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}
	
	/*public List<VFatContaHospitalarPac> pesquisarPaciente(
			Object strPesquisa) {		
		return this.faturamentoFacade.pesquisarPorPacCodigo((Integer)((StringUtils.isNotEmpty((String)strPesquisa))?Integer.valueOf((String)strPesquisa):null));
	}
	
	public Long pesquisarPacienteCount(Object strPesquisa) {		
		return this.faturamentoFacade.pesquisarPorPacCodigoCount((Integer)strPesquisa);
	}*/

	public DominioSituacaoConta[] getSituacaoesConta() {
		return new DominioSituacaoConta[] {DominioSituacaoConta.A, DominioSituacaoConta.F};
	}
	
	public Integer getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(Integer contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public DominioSituacaoConta getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoConta situacao) {
		this.situacao = situacao;
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}

	public VFatContaHospitalarPac getFatContaHospitalarPacSelecionado() {
		return fatContaHospitalarPacSelecionado;
	}

	public void setFatContaHospitalarPacSelecionado(
			VFatContaHospitalarPac fatContaHospitalarPacSelecionado) {
		this.fatContaHospitalarPacSelecionado = fatContaHospitalarPacSelecionado;
	}

	public DynamicDataModel<VFatContaHospitalarPac> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VFatContaHospitalarPac> dataModel) {
		this.dataModel = dataModel;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Instance<CodPacienteFoneticaVO> getCodPacienteFonetica() {
		return codPacienteFonetica;
	}

	public void setCodPacienteFonetica(
			Instance<CodPacienteFoneticaVO> codPacienteFonetica) {
		this.codPacienteFonetica = codPacienteFonetica;
	}
}
