package br.gov.mec.aghu.blococirurgico.portalplanejamento.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasSalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasTurno2VO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;


public class PortalPlanejamentoCirurgiasController extends ActionController {

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	private static final long serialVersionUID = 2180692121705212906L;
	
	private static final String ESCALA_SALAS = "consultaEscalaSalas";
	private static final String PORTAL_PESQUISA_CIRURGIAS = "portalPesquisaCirurgias";
	private static final String PORTAL_PLANEJAMENTO = "portalPlanejamentoCirurgias";
	private static final String DETALHAMENTO = "detalhamentoPortalAgendamento";
	private final static Short SALA_NAO_INFORMADA = Short.valueOf("-1");

	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroFacade;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@Inject
	private ConsultaEscalaSalasController consultaEscalaSalasController;
	
	@Inject
	private PortalPesquisaCirurgiasController portalPesquisaCirurgiasController;
	
	@Inject
	private DetalhamentoPortalAgendamentoController detalhamentoPortalAgendamentoController;
	
	private List<MbcSalaCirurgica> salasCirurgicasEquipe = new ArrayList<MbcSalaCirurgica>(); 
	
	private PortalPlanejamentoCirurgiasDiaVO diaVO;
	private PortalPlanejamentoCirurgiasSalaVO salaVO;
	private PortalPlanejamentoCirurgiasTurno2VO turnoVO;
	private List<PortalPlanejamentoCirurgiasTurno2VO> turnosVO;
	private PortalPlanejamentoCirurgiasDiaVO detalhamentoVO;
	private Integer dia;
	
	private final List<Integer> indicesDiasDaSemana  = Arrays.asList(0,1,2,3,4,5,6);
	
	//FILTRO
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dataInicio;
	private AghEspecialidades especialidade;	
	private MbcProfAtuaUnidCirgs equipe;
	private MbcSalaCirurgica salaCirurgica;
	private Boolean pesquisaRealizada = Boolean.FALSE;
	
	private Boolean togglePesquisaOpened;
	private Integer abaAberta;
	private String titleSlider;
	
	//se true indica que entrou na tela, ou seja,
	//não retornou de outra
	private boolean carregouTela = true;
	
	//Parametros Detalhamento
	private Short seqEspecialidade;
	private Integer matriculaEquipe;
	private Short vinCodigoEquipe;
	private Short unfSeqEquipe;
	private String indFuncaoProfEquipe;
	private Short seqUnidFuncionalCirugica;
	private String cameFrom;
	private Short unfSeqSala;
	private Short salaSeqp;
	private Long dataAgendaMili;
		
	public void iniciar() {
		titleSlider = "";
		if(diaVO != null) {
			obterTitleSlider();
			abaAberta = -1;
		} else {
			abaAberta = 0;
		}
		if (carregouTela) {
			setDataInicio(new Date());
			popularFiltrosPorUsuarioLogado();
			carregouTela = false;
		}
	}
	
	public void collapseTogglePesquisa() {
		if (abaAberta == null || (abaAberta != null && -1 == abaAberta)) {
			abaAberta = 0;
		} else {
			abaAberta = -1;
		}
	}
	
	public void limpar(){
		setUnidadeFuncional(null);
		setDataInicio(new Date());
		setEspecialidade(null);
		setEquipe(null);
		setSalaCirurgica(null);
		setPesquisaRealizada(Boolean.FALSE);
		titleSlider = "";
		abaAberta = 0;
		diaVO = null;
		popularFiltrosPorUsuarioLogado();
	}
	
		
	public void pesquisar(){
		try{
			setPesquisaRealizada(Boolean.TRUE);
			obterTitleSlider();
			diaVO = blocoCirurgicoPortalPlanejamentoFacade.pesquisarPortalPlanejamentoCirurgia(unidadeFuncional.getSeq(),dataInicio,especialidade,equipe,salaCirurgica, false,7, true);
			if(diaVO == null) {
				abaAberta = 0;
			}
		}catch (BaseException e) {
			diaVO = null;
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<PortalPlanejamentoCirurgiasTurno2VO> detalharSala(PortalPlanejamentoCirurgiasSalaVO salaVO, Integer dia) {
		this.salaVO = salaVO;
		this.dia = dia;
		return this.detalhar();
	}
	
	public List<PortalPlanejamentoCirurgiasTurno2VO> detalhar() {
		turnosVO = new ArrayList<PortalPlanejamentoCirurgiasTurno2VO>();
		try{
			List<MbcSalaCirurgica> salas = blocoCirurgicoPortalPlanejamentoFacade.buscarSalasCirurgicasAtivasPorUnfSeqSeqp(salaVO.getUnfSeq(), salaVO.getNumeroSala());
			detalhamentoVO = blocoCirurgicoPortalPlanejamentoFacade.pesquisarPortalPlanejamentoCirurgia(salaVO.getUnfSeq(),diaVO.getDatasAgendaDate()[dia],especialidade,equipe,salas.get(0), false,1, true);
			if(detalhamentoVO != null) {
					for(PortalPlanejamentoCirurgiasTurno2VO turno : detalhamentoVO.getListaSalas().get(0).getListaTurnos2()){
						turnosVO.add(turno);
					}
			}
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return turnosVO;
	}
	
	private void obterTitleSlider() {
		if(unidadeFuncional != null) {
			titleSlider = " | Unidade: ".concat(unidadeFuncional.getDescricao());
		}
		if(dataInicio != null) {
			titleSlider = titleSlider.concat(" | Data: ").concat(DateUtil.obterDataFormatada(dataInicio, "dd/MM/yyyy"));
		}
		if(especialidade != null) {
			titleSlider = titleSlider.concat(" | Especialidade: ").concat(especialidade.getNomeEspecialidade());
		}
		if(equipe != null) {
			titleSlider = titleSlider.concat(" | Equipe: ").concat(equipe.getRapServidores().getPessoaFisica().getNome());
		}
		if(salaCirurgica != null) {
			titleSlider = titleSlider.concat(" | Sala: ").concat(salaCirurgica.getId().getSeqp().toString());
		}
	}
	
	public List<PortalPlanejamentoCirurgiasTurno2VO> buscarTurnosDia(Integer dia, PortalPlanejamentoCirurgiasSalaVO sala){
		List<PortalPlanejamentoCirurgiasTurno2VO> turnos = new ArrayList<PortalPlanejamentoCirurgiasTurno2VO>();
		if (sala!=null){
			for(PortalPlanejamentoCirurgiasTurno2VO turno : sala.getListaTurnos2()){
				if(turno.getDia().equals(dia)){
					turnos.add(turno);
				}
			}
		}
		return turnos;
	}
	
	public Boolean getVerificarIndisponivel(Integer dia, PortalPlanejamentoCirurgiasSalaVO sala){
		Boolean verificar = false;
		if(sala != null && sala.getListaTurnos2() != null){
			for(PortalPlanejamentoCirurgiasTurno2VO turno : sala.getListaTurnos2()){
				if(turno.getDia().equals(dia) && !turno.getIndisponivel() && !turno.getBloqueado()){ //se houver algum turno não indisponivel, deve habilitar o clique para tela de detalhe
					verificar = true;
				}else{
					//se tiver algum agendamento  para o dia mesmo bloqueado, deve habilitar o clique para tela de detalhe
					if(turno.getDia().equals(dia)){
						if(turno.getListaAgendas()!=null){
							for (PortalPlanejamentoCirurgiasAgendaVO agenda : turno.getListaAgendas()) {
								if(agenda.getPlanejado()){
									verificar=true;
									break;
								}
							} 
						}
					}
				}
			}
		}
		
		return verificar;
	}
	
	public String redirecionarDetalhamento(Integer dia, PortalPlanejamentoCirurgiasSalaVO sala){
		if (getVerificarIndisponivel(dia,sala)){
			this.popularParametrosDetalhamento(dia,sala);
			List<PortalPlanejamentoCirurgiasSalaVO> portalSalas  =  diaVO.getListaSalas();
			this.salasCirurgicasEquipe = new ArrayList<MbcSalaCirurgica>();
			for (PortalPlanejamentoCirurgiasSalaVO portalPlanejamentoCirurgiasSalaVO : portalSalas) {
				//carregar sala cirurgica
				if(portalPlanejamentoCirurgiasSalaVO.getNumeroSala().equals(SALA_NAO_INFORMADA)) {
					continue;
				}
				MbcSalaCirurgica salaCirurgica = blocoCirurgicoCadastroFacade.obterSalaCirurgicaBySalaCirurgicaId(Short.valueOf(portalPlanejamentoCirurgiasSalaVO.getNumeroSala()), portalPlanejamentoCirurgiasSalaVO.getUnfSeq());
				this.salasCirurgicasEquipe.add(salaCirurgica);
			}
	
			detalhamentoPortalAgendamentoController.setSeqEspecialidade(seqEspecialidade);
			detalhamentoPortalAgendamentoController.setMatriculaEquipe(matriculaEquipe);
			detalhamentoPortalAgendamentoController.setVinCodigoEquipe(vinCodigoEquipe);
			detalhamentoPortalAgendamentoController.setUnfSeqEquipe(unfSeqEquipe);
			detalhamentoPortalAgendamentoController.setIndFuncaoProfEquipe(indFuncaoProfEquipe);
			detalhamentoPortalAgendamentoController.setSeqUnidFuncionalCirugica(seqUnidFuncionalCirugica);
			detalhamentoPortalAgendamentoController.setUnfSeqSala(unfSeqSala);
			detalhamentoPortalAgendamentoController.setSalaSeqp(salaSeqp);
			detalhamentoPortalAgendamentoController.setDataAgendaMili(dataAgendaMili);
			detalhamentoPortalAgendamentoController.setCameFrom(PORTAL_PLANEJAMENTO);
			//detalhamentoPortalAgendamentoController.setSalasCirurgicas(salasCirurgicasEquipe);
			detalhamentoPortalAgendamentoController.setSalasCirurgicasEquipe(salasCirurgicasEquipe);
			detalhamentoPortalAgendamentoController.iniciar();
			return DETALHAMENTO;
		} 
		return null;
	}
	
	private void popularParametrosDetalhamento(Integer dia, PortalPlanejamentoCirurgiasSalaVO sala){
		
		//inicializar
		inializarParametros();
		this.setCameFrom("portalPlanejamentoCirurgias");
		
		
		if(this.getEspecialidade()!=null){
			this.setSeqEspecialidade(this.getEspecialidade().getSeq());
		}
		if(this.getEquipe()!=null){
			this.setMatriculaEquipe(equipe.getId().getSerMatricula());
			this.setVinCodigoEquipe(equipe.getId().getSerVinCodigo());
			this.setIndFuncaoProfEquipe(equipe.getIndFuncaoProf().toString());
			this.setUnfSeqEquipe(equipe.getUnidadeFuncional().getSeq());
		}
		if(this.getUnidadeFuncional()!=null){
			this.setSeqUnidFuncionalCirugica(this.getUnidadeFuncional().getSeq());
		}
		
		if(sala.getNumeroSala() != null && sala.getUnfSeq() != null){
			this.setSalaSeqp(Short.valueOf(sala.getNumeroSala()));
			this.setUnfSeqSala(Short.valueOf(sala.getUnfSeq()));
			this.setSeqUnidFuncionalCirugica(Short.valueOf(sala.getUnfSeq()));
		}
		
		if(dia!=null){
		  Date datasAgendamento[] = this.diaVO.getDatasAgendaDate();
		//  this.dataInicio = datasAgendamento[dia];
		  this.dataAgendaMili = datasAgendamento[dia].getTime();
		}
	}
	
	private void inializarParametros(){
		this.setSeqEspecialidade(null);
		this.setMatriculaEquipe(null);
		this.setVinCodigoEquipe(null);
		this.setIndFuncaoProfEquipe(null);
		this.setUnfSeqEquipe(null);
		this.setSeqUnidFuncionalCirugica(null);
	}
	
	public List<AghEspecialidades> listarEspecialidades(String param) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarEspecialidadesSemEspSeq((String) param),listarEspecialidadesCount(param));
	}
	
	public Long listarEspecialidadesCount(String param) {
		return this.aghuFacade.pesquisarEspecialidadesSemEspSeqCount((String) param);
	}
	
	public List<AghUnidadesFuncionais> listarUnidades(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroFacade.buscarUnidadesFuncionaisCirurgia(objPesquisa),listarUnidadesCount(objPesquisa));
	}
	
	public Long listarUnidadesCount(String objPesquisa) {
		return blocoCirurgicoCadastroFacade.contarUnidadesFuncionaisCirurgia(unidadeFuncional);
	}
	
	public List<MbcSalaCirurgica> listarSalasCirurgicas(String objPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoPortalPlanejamentoFacade.buscarSalasCirurgicasAtivasPorUnfSeqSeqp(unidadeFuncional.getSeq(), 
				objPesquisa != null && StringUtils.isNotEmpty(objPesquisa) ? Short.valueOf(objPesquisa) : null), 
				listarSalasCirurgicasCount(objPesquisa));
	}
	
	public Long listarSalasCirurgicasCount(String objPesquisa) {
		return this.blocoCirurgicoPortalPlanejamentoFacade.buscarSalasCirurgicasAtivasPorUnfSeqSeqpCount(unidadeFuncional.getSeq(), 
				objPesquisa != null && StringUtils.isNotEmpty(objPesquisa) ? Short.valueOf(objPesquisa) : null);
	}
	
	public void limparSalasCirurgicas() {
		setUnidadeFuncional(null);
		setSalaCirurgica(null);
	}
	
	public void verificarEquipe(){
		if(this.getEquipe()!=null){
			List<MbcProfAtuaUnidCirgs>	listaEquipes = this.listarEquipes(this.getEquipe().getRapServidores().getPessoaFisica().getNome());
			if (listaEquipes!=null && listaEquipes.size()>0){
				this.equipe = listaEquipes.get(0);
			}
			else{
				this.equipe = null;
			}
		}
		
	}
	
	public List<MbcProfAtuaUnidCirgs> listarEquipes(String objPesquisa) {	
		return this.returnSGWithCount(blocoCirurgicoPortalPlanejamentoFacade.buscarEquipeMedicaParaAgendamento((String) objPesquisa, 
				(unidadeFuncional != null) ? unidadeFuncional.getSeq() : null, (especialidade != null) ? especialidade.getSeq() : null), 
				this.listarEquipesCount(objPesquisa));
	}
	
	public Long listarEquipesCount(String objPesquisa) {
		return blocoCirurgicoPortalPlanejamentoFacade.buscarEquipeMedicaParaAgendamentoCount((String) objPesquisa, 
				(unidadeFuncional != null) ? unidadeFuncional.getSeq() : null, (especialidade != null) ? especialidade.getSeq() : null);
	}
	
	public void retroceder() {
		try{
			Date data = DateUtil.adicionaDias(diaVO.getDatasAgendaDate()[0], -1);
			
			diaVO = blocoCirurgicoPortalPlanejamentoFacade.pesquisarPortalPlanejamentoCirurgia(unidadeFuncional.getSeq(),data,especialidade,equipe,salaCirurgica, true,7, true);
			dataInicio = diaVO.getDatasAgendaDate()[0];
			obterTitleSlider();
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void avancar() {
		try{
			Date data = DateUtil.adicionaDias(diaVO.getDatasAgendaDate()[6], 1);
			diaVO = blocoCirurgicoPortalPlanejamentoFacade.pesquisarPortalPlanejamentoCirurgia(unidadeFuncional.getSeq(),data,especialidade,equipe,salaCirurgica, false,7, true);
			dataInicio = diaVO.getDatasAgendaDate()[0];
			obterTitleSlider();
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String consultarEscala() {
		consultaEscalaSalasController.setUrlVoltar(PORTAL_PLANEJAMENTO);
		consultaEscalaSalasController.setUnfSeq(this.getUnidadeFuncional().getSeq());
		return ESCALA_SALAS;
	}
	
	public String navegarPortalPesquisa() {
		portalPesquisaCirurgiasController.setVoltarPara(PORTAL_PLANEJAMENTO);
		return PORTAL_PESQUISA_CIRURGIAS;
	}
	
	public String pintarProgressBar(PortalPlanejamentoCirurgiasTurno2VO turno) {
		if(turno.getBloqueado() != null && turno.getBloqueado()) {
			return "agenda-bloqueada";
		} else if(turno.getIndisponivel() != null && turno.getIndisponivel()) {
			return "agenda-indisponivel";
		} else if(turno.getOverbooking() != null && turno.getOverbooking()) {
			return "agenda-overbooking";
		} else if(turno.getCedencia()) {
			return "agenda-cedencia";
		}
		return "";
	}
	
	public void popularFiltrosPorUsuarioLogado() {
		MbcProfAtuaUnidCirgs eqp = new MbcProfAtuaUnidCirgs();
						
		try {			
			eqp = blocoCirurgicoPortalPlanejamentoFacade.buscarEquipesPorUsuarioLogado();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		if (eqp != null) {
			setEquipe(eqp.getMbcProfAtuaUnidCirgs());
			setUnidadeFuncional(eqp.getUnidadeFuncional());
			if (eqp.getRapServidores().getEquipes()!=null && !eqp.getRapServidores().getEquipes().isEmpty()){
				setEspecialidade(aghuFacade.obterEspecialidadePorEquipe(eqp.getRapServidores().getEquipes().iterator().next().getSeq()));
			}	
			setSalaCirurgica(null);
		}
	}
	
	/**
	 * GETS AND SETS
	 */

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public MbcSalaCirurgica getSalaCirurgica() {
		return salaCirurgica;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	public MbcProfAtuaUnidCirgs getEquipe() {
		return equipe;
	}

	public void setEquipe(MbcProfAtuaUnidCirgs equipe) {
		this.equipe = equipe;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public PortalPlanejamentoCirurgiasDiaVO getDiaVO() {
		return diaVO;
	}

	public void setDiaVO(PortalPlanejamentoCirurgiasDiaVO diaVO) {
		this.diaVO = diaVO;
	}

	public Boolean getPesquisaRealizada() {
		return pesquisaRealizada;
	}

	public void setPesquisaRealizada(Boolean pesquisaRealizada) {
		this.pesquisaRealizada = pesquisaRealizada;
		if (pesquisaRealizada) {
			abaAberta = -1;
		} else {
			abaAberta = 0;
		}
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public Integer getMatriculaEquipe() {
		return matriculaEquipe;
	}

	public void setMatriculaEquipe(Integer matriculaEquipe) {
		this.matriculaEquipe = matriculaEquipe;
	}

	public Short getVinCodigoEquipe() {
		return vinCodigoEquipe;
	}

	public void setVinCodigoEquipe(Short vinCodigoEquipe) {
		this.vinCodigoEquipe = vinCodigoEquipe;
	}

	public Short getUnfSeqEquipe() {
		return unfSeqEquipe;
	}

	public void setUnfSeqEquipe(Short unfSeqEquipe) {
		this.unfSeqEquipe = unfSeqEquipe;
	}

	public String getIndFuncaoProfEquipe() {
		return indFuncaoProfEquipe;
	}

	public void setIndFuncaoProfEquipe(String indFuncaoProfEquipe) {
		this.indFuncaoProfEquipe = indFuncaoProfEquipe;
	}

	public Short getSeqUnidFuncionalCirugica() {
		return seqUnidFuncionalCirugica;
	}

	public void setSeqUnidFuncionalCirugica(Short seqUnidFuncionalCirugica) {
		this.seqUnidFuncionalCirugica = seqUnidFuncionalCirugica;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Short getUnfSeqSala() {
		return unfSeqSala;
	}

	public void setUnfSeqSala(Short unfSeqSala) {
		this.unfSeqSala = unfSeqSala;
	}

	public Short getSalaSeqp() {
		return salaSeqp;
	}

	public void setSalaSeqp(Short salaSeqp) {
		this.salaSeqp = salaSeqp;
	}

	public void setCarregouTela(Boolean carregouTela) {
		this.carregouTela = carregouTela;
	}

	public Boolean getCarregouTela() {
		return carregouTela;
	}		

	public Long getDataAgendaMili() {
		return dataAgendaMili;
	}

	public void setDataAgendaMili(Long dataAgendaMili) {
		this.dataAgendaMili = dataAgendaMili;
	}

	public Boolean getTogglePesquisaOpened() {
		return togglePesquisaOpened;
	}

	public void setTogglePesquisaOpened(Boolean togglePesquisaOpened) {
		this.togglePesquisaOpened = togglePesquisaOpened;
	}

	public String getTitleSlider() {
		return titleSlider;
	}

	public void setTitleSlider(String titleSlider) {
		this.titleSlider = titleSlider;
	}

	public Integer getAbaAberta() {
		return abaAberta;
	}

	public void setAbaAberta(Integer abaAberta) {
		this.abaAberta = abaAberta;
	}

	public List<Integer> getIndicesDiasDaSemana() {
		return indicesDiasDaSemana;
	}

	public PortalPlanejamentoCirurgiasSalaVO getSalaVO() {
		return salaVO;
	}

	public void setSalaVO(PortalPlanejamentoCirurgiasSalaVO salaVO) {
		this.salaVO = salaVO;
	}

	public PortalPlanejamentoCirurgiasTurno2VO getTurnoVO() {
		return turnoVO;
	}

	public void setTurnoVO(PortalPlanejamentoCirurgiasTurno2VO turnoVO) {
		this.turnoVO = turnoVO;
	}

	public PortalPlanejamentoCirurgiasDiaVO getDetalhamentoVO() {
		return detalhamentoVO;
	}

	public void setDetalhamentoVO(PortalPlanejamentoCirurgiasDiaVO detalhamentoVO) {
		this.detalhamentoVO = detalhamentoVO;
	}

	public List<PortalPlanejamentoCirurgiasTurno2VO> getTurnosVO() {
		return turnosVO;
	}

	public void setTurnosVO(List<PortalPlanejamentoCirurgiasTurno2VO> turnosVO) {
		this.turnosVO = turnosVO;
	}

	public Integer getDia() {
		return dia;
	}

	public void setDia(Integer dia) {
		this.dia = dia;
	}
}
