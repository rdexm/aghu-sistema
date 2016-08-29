package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.PdtMedicUsual;
import br.gov.mec.aghu.model.PdtMedicUsualId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class MedicamentosUnidadeCirurgicaController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 1938842027342597891L;

	private static final String MEDICAMENTOS_UNIDADE = "medicamentosUnidadeCirurgica";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	private PdtMedicUsual pdtMedicUsual = new PdtMedicUsual();
	private PdtMedicUsual pdtMedicUsualDelecao;
	private PdtMedicUsualId id;
	private List<PdtMedicUsual> listaMedicamentosUsuais;
	
	private boolean emEdicao;
	private boolean pesquisou;
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

	 
	
		if (!pesquisou){
			this.limparPesquisa();
		}
	
	}
	
	
	//Sugestion Unidade
	public List<AghUnidadesFuncionais> listarUnidades(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.buscarUnidadesFuncionaisCirurgia(objPesquisa),listarUnidadesCount(objPesquisa));
	}
	
	public Long listarUnidadesCount(String objPesquisa) {
		return blocoCirurgicoCadastroApoioFacade.contarUnidadesFuncionaisCirurgia(objPesquisa);
	}
	
	//Sugestion Medicamento
	public List<AfaMedicamento> pesquisarMedicamentosAtivos(String objPesquisa) {
		return this.returnSGWithCount(farmaciaFacade.pesquisarMedicamentosAtivos(objPesquisa),pesquisarMedicamentosAtivosCount(objPesquisa));
	}

	public Long pesquisarMedicamentosAtivosCount(String objPesquisa) {
		return farmaciaFacade.pesquisarMedicamentosAtivosCount(objPesquisa);
	}

	public String pesquisar() {
		listaMedicamentosUsuais = blocoCirurgicoProcDiagTerapFacade.pesquisaPdtMedicUsual(this.getPdtMedicUsual());
		this.setPesquisou(true);
		this.setEmEdicao(false);
		return MEDICAMENTOS_UNIDADE;
	}
	
	public String limparPesquisa() {
		limparCadastro();
		this.setPdtMedicUsual(new PdtMedicUsual());
		this.getPdtMedicUsual().setId(new PdtMedicUsualId());
		listaMedicamentosUsuais = null;
		this.setEmEdicao(false);
		this.setPesquisou(false);	
		return MEDICAMENTOS_UNIDADE;  
	}
	
	public void limparPosGravacao(){
		AghUnidadesFuncionais unid = getPdtMedicUsual().getAghUnidadesFuncionais();
		limparCadastro();
		getPdtMedicUsual().setAghUnidadesFuncionais(unid);		
	}
	
	private void limparCadastro() {
		PdtMedicUsual pdtMedicUsual = new PdtMedicUsual();
		PdtMedicUsualId pdtMedicUsualId = new PdtMedicUsualId();
		pdtMedicUsual.setId(pdtMedicUsualId);
		this.setPdtMedicUsual(pdtMedicUsual);
		this.setEmEdicao(false);
		id = null;
	}
	
	public void gravar() {
		
		boolean novo = this.getPdtMedicUsual().getId() == null || this.getPdtMedicUsual().getId().getUnfSeq() == null;
		try {
			//Seta a unidade cirurgica
			if (novo) {
				getPdtMedicUsual().getId().setUnfSeq(getPdtMedicUsual().getAghUnidadesFuncionais().getSeq());
				getPdtMedicUsual().getId().setMedMatCodigo(getPdtMedicUsual().getAfaMedicamento().getMatCodigo());
			}
			String msgRetorno = this.blocoCirurgicoProcDiagTerapFacade.persistirPdtMedicUsual(this.getPdtMedicUsual());
			
			this.apresentarMsgNegocio(Severity.INFO, msgRetorno, this.getPdtMedicUsual().getAfaMedicamento().getDescricao());

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.limparPosGravacao();
		this.pesquisar();
	}
	

	public void editar( ) {
		this.setEmEdicao(true);
	}
	
	public void limparEdicao( ) {
		this.setEmEdicao(false);
	}
	
	public void excluir() {
		try {
			String msgRetorno = this.blocoCirurgicoProcDiagTerapFacade.excluirPdtMedicUsual(pdtMedicUsualDelecao);
			this.apresentarMsgNegocio(Severity.INFO,msgRetorno);
			this.pesquisar();
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public PdtMedicUsual getPdtMedicUsual() {
		return pdtMedicUsual;
	}

	public void setPdtMedicUsual(PdtMedicUsual pdtMedicUsual) {
		this.pdtMedicUsual = pdtMedicUsual;
	}

	public PdtMedicUsualId getId() {
		return id;
	}

	public void setId(PdtMedicUsualId id) {
		this.id = id;
	}

	public List<PdtMedicUsual> getListaMedicamentosUsuais() {
		return listaMedicamentosUsuais;
	}

	public void setListaMedicamentosUsuais(
			List<PdtMedicUsual> listaMedicamentosUsuais) {
		this.listaMedicamentosUsuais = listaMedicamentosUsuais;
	}
	
	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}


	public boolean isPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

	public void setPdtMedicUsualDelecao(PdtMedicUsual pdtMedicUsualDelecao) {
		this.pdtMedicUsualDelecao = pdtMedicUsualDelecao;
	}

	public PdtMedicUsual getPdtMedicUsualDelecao() {
		return pdtMedicUsualDelecao;
	}

}