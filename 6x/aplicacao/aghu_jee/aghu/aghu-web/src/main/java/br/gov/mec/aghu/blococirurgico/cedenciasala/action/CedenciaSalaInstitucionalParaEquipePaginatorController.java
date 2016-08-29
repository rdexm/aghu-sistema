package br.gov.mec.aghu.blococirurgico.cedenciasala.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.cedenciasala.business.IBlocoCirurgicoCedenciaSalaFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpaId;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CedenciaSalaInstitucionalParaEquipePaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 inicio();
	}

	@Inject @Paginator
	private DynamicDataModel<MbcCedenciaSalaHcpa> dataModel;

		private static final long serialVersionUID = -5333050994713383226L;
	
	
	@EJB
	private IBlocoCirurgicoCedenciaSalaFacade blocoCirurgicoCedenciaSalaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private MbcCedenciaSalaHcpa mbcCedenciaSala;
	private LinhaReportVO equipe;
	private Boolean habilitarSuggestionEquipe;
	

	private final String PAGE_PROGRAMAR_CEDENCIA = "cedenciaSalaInstitucionalParaEquipeAgendamento";
	
	public void inicio(){
		if(mbcCedenciaSala == null){
			mbcCedenciaSala = new MbcCedenciaSalaHcpa();
			mbcCedenciaSala.setId(new MbcCedenciaSalaHcpaId());
		}
	}
	public void efetuarPesquisa(){
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		mbcCedenciaSala = new MbcCedenciaSalaHcpa();
		mbcCedenciaSala.setId(new MbcCedenciaSalaHcpaId());
		equipe = null;
		this.dataModel.limparPesquisa();	
		desabilitarSuggestionEquipe();
	}
	
	public void ativarInativar(MbcCedenciaSalaHcpa cedenciaSelecionada){
		try {
			if (cedenciaSelecionada.getIndSituacao().isAtivo()){
				blocoCirurgicoCedenciaSalaFacade.verificarProgramacaoAgendaSalaInstitucional(cedenciaSelecionada);
			}
			blocoCirurgicoCedenciaSalaFacade.ativarInativarMbcCedenciaSalaHcpa(cedenciaSelecionada);			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MBC_CEDENCIA_SALA_ATUALIZADO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String programarCedencia(){
		return PAGE_PROGRAMAR_CEDENCIA;
	}
	
	
	public List<LinhaReportVO> pesquisarEquipes(String pesquisa){
		return this.returnSGWithCount(blocoCirurgicoCedenciaSalaFacade
				.pesquisarNomeMatVinCodUnfByVMbcProfServ(pesquisa,
						mbcCedenciaSala.getUnidade().getSeq(), DominioSituacao.A,
						DominioFuncaoProfissional.MPF,
						DominioFuncaoProfissional.MCO),pesquisarEquipesCount(pesquisa));
	}
	
	public Long pesquisarEquipesCount(String pesquisa){
		return blocoCirurgicoCedenciaSalaFacade
				.pesquisarNomeMatVinCodUnfByVMbcProfServCount(
						pesquisa, mbcCedenciaSala.getUnidade().getSeq(), DominioSituacao.A,
						DominioFuncaoProfissional.MPF,
						DominioFuncaoProfissional.MCO);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesCirurgicas(String pesquisa){
		return aghuFacade.pesquisarUnidadesFuncionaisUnidadeExecutoraCirurgias(pesquisa);
	}
	
	public void habilitarSuggestionEquipe(){
		habilitarSuggestionEquipe = true;
	}
	
	public void desabilitarSuggestionEquipe(){
		habilitarSuggestionEquipe = false;
		equipe = null;
	}
	
	@Override
	public Long recuperarCount() {
		return blocoCirurgicoCedenciaSalaFacade.pesquisarCedenciasProgramadasCount(mbcCedenciaSala, equipe);
	}

	@Override
	public List<MbcCedenciaSalaHcpa> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String order, boolean asc) {
		return blocoCirurgicoCedenciaSalaFacade.pesquisarCedenciasProgramadas(mbcCedenciaSala, equipe, firstResult, maxResult, order, false);
	}
	public MbcCedenciaSalaHcpa getMbcCedenciaSala() {
		return mbcCedenciaSala;
	}
	public void setMbcCedenciaSala(MbcCedenciaSalaHcpa mbcCedenciaSala) {
		this.mbcCedenciaSala = mbcCedenciaSala;
	}
	public LinhaReportVO getEquipe() {
		return equipe;
	}
	public void setEquipe(LinhaReportVO equipe) {
		this.equipe = equipe;
	}
	public void setHabilitarSuggestionEquipe(Boolean habilitarSuggestionEquipe) {
		this.habilitarSuggestionEquipe = habilitarSuggestionEquipe;
	}
	public Boolean getHabilitarSuggestionEquipe() {
		return habilitarSuggestionEquipe;
	}

 


	public DynamicDataModel<MbcCedenciaSalaHcpa> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcCedenciaSalaHcpa> dataModel) {
	 this.dataModel = dataModel;
	}
}
