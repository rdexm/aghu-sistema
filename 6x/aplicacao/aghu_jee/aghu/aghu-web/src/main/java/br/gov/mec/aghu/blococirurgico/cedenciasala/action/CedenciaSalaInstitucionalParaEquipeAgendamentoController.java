package br.gov.mec.aghu.blococirurgico.cedenciasala.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.cedenciasala.business.IBlocoCirurgicoCedenciaSalaFacade;
import br.gov.mec.aghu.blococirurgico.cedenciasala.vo.CedenciaSalaInstitucionalParaEquipeVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CedenciaSalaInstitucionalParaEquipeAgendamentoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 inicio();
	}

		private static final long serialVersionUID = -8975828574663389633L;

	@EJB
	private IBlocoCirurgicoCedenciaSalaFacade blocoCirurgicoCedenciaSalaFacade;
	
	private CedenciaSalaInstitucionalParaEquipeVO cedenciaSala;
	private MbcCaracteristicaSalaCirg caracteristicaSalaCirg;
	private Date dataAnterior;
	private AghEspecialidades aghEspecialidades;
	
	private final String PAGE_CEDENCIA_SALA_ENTRE_EQUIPES = "cedenciaSalaInstitucionalParaEquipe";

	public void inicio() {
	 

	 

		cedenciaSala = new CedenciaSalaInstitucionalParaEquipeVO();
		cedenciaSala.setIntervalo(1);
	
	}
	
	
	public List<MbcCaracteristicaSalaCirg> buscarSalasCirurgicas(String objPesquisa){		
		return this.returnSGWithCount(blocoCirurgicoCedenciaSalaFacade.listarCaracteristicaSalaCirgPorDiaSemana(objPesquisa, cedenciaSala.getData()),
                buscarSalasCirurgicasCount(objPesquisa));
	}

    public Long buscarSalasCirurgicasCount(String objPesquisa){
        return blocoCirurgicoCedenciaSalaFacade.pesquisaCaracteristicaSalaCirgPorDiaSemanaCount(objPesquisa, cedenciaSala.getData());
    }

	public void setarCaracteristicaSalaCirg(){
		cedenciaSala.setCasSeq(caracteristicaSalaCirg.getSeq());
		cedenciaSala.setUnidade(caracteristicaSalaCirg.getUnidadeSalaCirurgica());
		cedenciaSala.setSala(caracteristicaSalaCirg.getMbcSalaCirurgica());
		cedenciaSala.setDiaSemana(caracteristicaSalaCirg.getDiaSemana());
		cedenciaSala.setTurno(caracteristicaSalaCirg.getMbcTurnos());
	}
	
	public void limparCaracteristicaSalaCirg(){
		caracteristicaSalaCirg = null;
		cedenciaSala.setCasSeq(null);
		cedenciaSala.setUnidade(null);
		cedenciaSala.setSala(null);
		cedenciaSala.setDiaSemana(null);
		cedenciaSala.setTurno(null);
		cedenciaSala.setEquipe(null);
		setAghEspecialidades(null);
	}
	
	public void limparCaracteristicaEquipe(){
		cedenciaSala.setEquipe(null);
		setAghEspecialidades(null);
	}
	
	public void limparCaracteristicaSalaCirgData(){
		if(cedenciaSala.getData() != null && !cedenciaSala.getData().equals(dataAnterior)){
			caracteristicaSalaCirg = null;
			cedenciaSala.setCasSeq(null);
			cedenciaSala.setUnidade(null);
			cedenciaSala.setSala(null);
			cedenciaSala.setDiaSemana(null);
			cedenciaSala.setTurno(null);
			cedenciaSala.setEquipe(null);
			dataAnterior = cedenciaSala.getData();
			setAghEspecialidades(null);
		}
	}
	
	public String gravar() {
		// Valida a data, agora setado com o requidedFake para não exibir o erro de obrigatoriedade nas requisições ajax.
		if (cedenciaSala.getData() == null) {
			apresentarMsgNegocio("data", Severity.ERROR, "CAMPO_OBRIGATORIO", "Data");
		} else {
			try {
				cedenciaSala.setUsuarioLogado(obterLoginUsuarioLogado());
				Integer recorrencias = blocoCirurgicoCedenciaSalaFacade.gravarCedenciaDeSalaInstitucionalParaEquipe(cedenciaSala,caracteristicaSalaCirg, this.aghEspecialidades);
				if(recorrencias == 0){
					apresentarMsgNegocio(Severity.INFO, "CEDENCIA_DE_SALA_INSTITUCIONAL_PARA_EQUIPE_GRAVACAO_SUCESSO");
				}else{
					apresentarMsgNegocio(Severity.INFO, "CEDENCIA_DE_SALA_INSTITUCIONAL_PARA_EQUIPE_GRAVACAO_SUCESSO_RECORRENCIAS", recorrencias);
				}
                return this.voltar();
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		return null;
	}
	
	public String voltar() {
		this.limparCaracteristicaSalaCirg();
		return PAGE_CEDENCIA_SALA_ENTRE_EQUIPES;
	}
	
	
	public List<LinhaReportVO> pesquisarEquipes(String pesquisa){
		return this.returnSGWithCount(blocoCirurgicoCedenciaSalaFacade
				.pesquisarNomeMatVinCodUnfByVMbcProfServ(pesquisa,
						cedenciaSala.getUnidade().getSeq(), DominioSituacao.A,
						DominioFuncaoProfissional.MPF,
						DominioFuncaoProfissional.MCO),pesquisarEquipesCount(pesquisa));
	}
	
	public Long pesquisarEquipesCount(String pesquisa){
		return blocoCirurgicoCedenciaSalaFacade
				.pesquisarNomeMatVinCodUnfByVMbcProfServCount(
						pesquisa, cedenciaSala.getUnidade().getSeq(), DominioSituacao.A,
						DominioFuncaoProfissional.MPF,
						DominioFuncaoProfissional.MCO);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidades(String pesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoCedenciaSalaFacade.pesquisarEspecialidadePorVbmcProfServidor((String) pesquisa,
																			this.cedenciaSala.getEquipe().getNumero11(),
																			this.cedenciaSala.getEquipe().getNumero4(),
																			this.cedenciaSala.getEquipe().getNumero5()),
																			pesquisarEspecialidadesCount((String) pesquisa));
	}
	
	public Long pesquisarEspecialidadesCount(String pesquisa){
		if(this.cedenciaSala.getEquipe() != null){
			return this.blocoCirurgicoCedenciaSalaFacade.pesquisarEspecialidadePorVbmcProfServidorCount(pesquisa,
														this.cedenciaSala.getEquipe().getNumero11(),
														this.cedenciaSala.getEquipe().getNumero4(),
														this.cedenciaSala.getEquipe().getNumero5());
		}
		return null;
	}
	
	
	public void obterEspecialidadeUnica(){
		if(this.cedenciaSala.getEquipe() != null){
			if(pesquisarEspecialidadesCount(StringUtils.EMPTY).equals(Long.valueOf("1"))){
				aghEspecialidades = this.blocoCirurgicoCedenciaSalaFacade.pesquisarEspecialidadePorVbmcProfServidor(StringUtils.EMPTY, 
																		 this.cedenciaSala.getEquipe().getNumero11(), 
																		 this.cedenciaSala.getEquipe().getNumero4(),
																		 this.cedenciaSala.getEquipe().getNumero5()).get(0);
			}
		}
	}
	
	// Getters e Setters
	public void setBlocoCirurgicoCedenciaSalaFacade(
			IBlocoCirurgicoCedenciaSalaFacade blocoCirurgicoCedenciaSalaFacade) {
		this.blocoCirurgicoCedenciaSalaFacade = blocoCirurgicoCedenciaSalaFacade;
	}

	public IBlocoCirurgicoCedenciaSalaFacade getBlocoCirurgicoCedenciaSalaFacade() {
		return blocoCirurgicoCedenciaSalaFacade;
	}

	public void setCedenciaSala(CedenciaSalaInstitucionalParaEquipeVO cedenciaSala) {
		this.cedenciaSala = cedenciaSala;
	}

	public CedenciaSalaInstitucionalParaEquipeVO getCedenciaSala() {
		return cedenciaSala;
	}

	public void setCaracteristicaSalaCirg(MbcCaracteristicaSalaCirg caracteristicaSalaCirg) {
		this.caracteristicaSalaCirg = caracteristicaSalaCirg;
	}

	public MbcCaracteristicaSalaCirg getCaracteristicaSalaCirg() {
		return caracteristicaSalaCirg;
	}

	public void setDataAnterior(Date dataAnterior) {
		this.dataAnterior = dataAnterior;
	}

	public Date getDataAnterior() {
		return dataAnterior;
	}


	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}


	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}	
}