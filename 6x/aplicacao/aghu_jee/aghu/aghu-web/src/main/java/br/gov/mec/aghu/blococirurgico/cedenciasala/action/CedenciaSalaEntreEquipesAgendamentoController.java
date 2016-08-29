package br.gov.mec.aghu.blococirurgico.cedenciasala.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cedenciasala.business.IBlocoCirurgicoCedenciaSalaFacade;
import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalasEntreEquipesEquipeVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CedenciaSalaEntreEquipesAgendamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		inicio();
	}

	private static final long serialVersionUID = -8975828574663389633L;

	@EJB
	private IBlocoCirurgicoCedenciaSalaFacade blocoCirurgicoCedenciaSalaFacade;

	private MbcCaractSalaEsp mbcCaractSalaEsp;
	private CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO;
	private Date dataAnterior;
	private final String PAGE_CEDENCIA_SALA_ENTRE_EQUIPES = "cedenciaSalaEntreEquipes";
	private List<AghEspecialidades> listaProfSubEsp;
	private AghEspecialidades aghEspecialidades; 

	public void inicio() {
		if(cedenciaSalasEntreEquipesEquipeVO == null){
			cedenciaSalasEntreEquipesEquipeVO = new CedenciaSalasEntreEquipesEquipeVO();
			cedenciaSalasEntreEquipesEquipeVO.setIntervalo(1);
		}
	}

	public List<MbcCaractSalaEsp> listarMbcCaractSalaEspPorDiaSemana(String objPesquisa){
		return this.returnSGWithCount(blocoCirurgicoCedenciaSalaFacade
                .listarMbcCaractSalaEspPorDiaSemana(objPesquisa, cedenciaSalasEntreEquipesEquipeVO.getData()),
                pesquisarMbcCaractSalaEspPorDiaSemanaCount(objPesquisa));
	}

    public Long pesquisarMbcCaractSalaEspPorDiaSemanaCount(String objPesquisa) {
        return blocoCirurgicoCedenciaSalaFacade.pesquisarMbcCaractSalaEspPorDiaSemanaCount(objPesquisa, cedenciaSalasEntreEquipesEquipeVO.getData());
    }

	public String gravar() {

		// Valida a data, agora setado com o requidedFake para não exibir o erro de obrigatoriedade nas requisições ajax.
		if (cedenciaSalasEntreEquipesEquipeVO.getData() == null) {
			apresentarMsgNegocio("data", Severity.ERROR, "CAMPO_OBRIGATORIO", "Data");			
			/*	apresentarMsgNegocioToControlFromResourceBundle("data", Severity.ERROR, "CAMPO_OBRIGATORIO", "Data");*/
		} else {
			setaEspecialidadeEquipeSub();
			try {
				cedenciaSalasEntreEquipesEquipeVO.setUsuarioLogado(obterLoginUsuarioLogado());
				Integer recorrencias = blocoCirurgicoCedenciaSalaFacade.gravarMbcSubstEscalaSala(mbcCaractSalaEsp, cedenciaSalasEntreEquipesEquipeVO);
				if(recorrencias == 0){
					apresentarMsgNegocio(Severity.INFO, "CEDENCIA_DE_SALA_ENTRE_EQUIPE_EQUIPE_GRAVACAO_SUCESSO");
				}else{
					apresentarMsgNegocio(Severity.INFO, "CEDENCIA_DE_SALA_ENTRE_EQUIPE_EQUIPE_GRAVACAO_SUCESSO_RECORRENCIAS", recorrencias);
				}
                return this.voltar();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return null;
	}
	
	public void limparCampos() {
		mbcCaractSalaEsp = null;
		cedenciaSalasEntreEquipesEquipeVO.setEquipeSubstituta(null);
		aghEspecialidades =null;
	}
	
	public void limparCamposData() {
		if(cedenciaSalasEntreEquipesEquipeVO.getData() != null && !cedenciaSalasEntreEquipesEquipeVO.getData().equals(dataAnterior)){
			mbcCaractSalaEsp = null;
			cedenciaSalasEntreEquipesEquipeVO.setEquipeSubstituta(null);
			dataAnterior = cedenciaSalasEntreEquipesEquipeVO.getData();
			aghEspecialidades =null;
		}
	}
	
	public void limpaCampoEspSub(){
		aghEspecialidades =null;
	}
	
	public String voltar() {
		limparCampos();
		cedenciaSalasEntreEquipesEquipeVO = null;		
		return PAGE_CEDENCIA_SALA_ENTRE_EQUIPES;
	}
	
	public List<LinhaReportVO> pesquisarEquipes(String pesquisa){
		return this.returnSGWithCount(blocoCirurgicoCedenciaSalaFacade
				.pesquisarNomeMatVinCodUnfByVMbcProfServ(pesquisa,
						mbcCaractSalaEsp.getMbcCaracteristicaSalaCirg().getUnidadeSalaCirurgica().getSeq(), 
						DominioSituacao.A,
						DominioFuncaoProfissional.MPF,
						DominioFuncaoProfissional.MCO),pesquisarEquipesCount(pesquisa));
	}
	
	public Long pesquisarEquipesCount(String pesquisa){
		return blocoCirurgicoCedenciaSalaFacade
				.pesquisarNomeMatVinCodUnfByVMbcProfServCount(pesquisa,
						mbcCaractSalaEsp.getMbcCaracteristicaSalaCirg().getUnidadeSalaCirurgica().getSeq(), 
						DominioSituacao.A,
						DominioFuncaoProfissional.MPF,
						DominioFuncaoProfissional.MCO);
	}
	
	public List<AghEspecialidades> pesquisarSiglaEspecialidadeSubstituta(String param){
		LinhaReportVO equipeSubstituta  = null;
		
		if(cedenciaSalasEntreEquipesEquipeVO != null){
			equipeSubstituta = cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta();
		}
		return this.returnSGWithCount(blocoCirurgicoCedenciaSalaFacade.pesquisarSiglaEspecialidadeSubstituta(param, equipeSubstituta),
									  blocoCirurgicoCedenciaSalaFacade.pesquisarSiglaEspecialidadeSubstitutaCount(param, equipeSubstituta));
	}
	
	public void poSelectPesquisarSiglaEspecialidadeSubstituta(){
		this.listaProfSubEsp = blocoCirurgicoCedenciaSalaFacade.pesquisarSiglaEspecialidadeSubstituta(null, cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta());
		if(listaProfSubEsp.size() == 1){
			setAghEspecialidades(listaProfSubEsp.get(0));
		}
	}
	
	public void setaEspecialidadeEquipeSub(){
		cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().setTexto2(aghEspecialidades.getSigla());
		cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().setTexto3(aghEspecialidades.getNomeEspecialidade());
		cedenciaSalasEntreEquipesEquipeVO.getEquipeSubstituta().setNumero12(aghEspecialidades.getSeq());
	}
	
	// Getters e Setters
	
	public void setMbcCaractSalaEsp(MbcCaractSalaEsp mbcCaractSalaEsp) {
		this.mbcCaractSalaEsp = mbcCaractSalaEsp;
	}
	
	public MbcCaractSalaEsp getMbcCaractSalaEsp() {
		return mbcCaractSalaEsp;
	}

	public void setCedenciaSalasEntreEquipesEquipeVO(
			CedenciaSalasEntreEquipesEquipeVO cedenciaSalasEntreEquipesEquipeVO) {
		this.cedenciaSalasEntreEquipesEquipeVO = cedenciaSalasEntreEquipesEquipeVO;
	}

	public CedenciaSalasEntreEquipesEquipeVO getCedenciaSalasEntreEquipesEquipeVO() {
		return cedenciaSalasEntreEquipesEquipeVO;
	}

	public IBlocoCirurgicoCedenciaSalaFacade getBlocoCirurgicoCedenciaSalaFacade() {
		return blocoCirurgicoCedenciaSalaFacade;
	}

	public void setBlocoCirurgicoCedenciaSalaFacade(
			IBlocoCirurgicoCedenciaSalaFacade blocoCirurgicoCedenciaSalaFacade) {
		this.blocoCirurgicoCedenciaSalaFacade = blocoCirurgicoCedenciaSalaFacade;
	}

	public void setDataAnterior(Date dataAnterior) {
		this.dataAnterior = dataAnterior;
	}

	public Date getDataAnterior() {
		return dataAnterior;
	}

	public List<AghEspecialidades> getLista() {
		return listaProfSubEsp;
	}

	public void setLista(List<AghEspecialidades> lista) {
		this.listaProfSubEsp = lista;
	}

	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}
	
}
