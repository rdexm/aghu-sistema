package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.vo.SalaCirurgicaVO;
import br.gov.mec.aghu.configuracao.vo.EquipeVO;
import br.gov.mec.aghu.dominio.DominioConselho;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoProfNascsId;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;


/**
 * Controller Incluir/Excluir Agendamento
 * 
 * @author fsantos
 * 
 */
public class RegistrarGestacaoAbaNascimentoFieldsetAgendamentoController extends ActionController  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6589546988357451478L;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private RegistrarGestacaoAbaNascimentoController registrarGestacaoAbaNascimentoController;
	
	private DominioConselho conselho;
	private RapServidorConselhoVO rapServidorConselhoVO;
	private RapServidorConselhoVO removerRapServidorConselhoVO;
	private Integer nasGsoPacCodigo;
	private Short nasGsoSeqp;
	private Integer nasSeqp;
	private Integer numeroConsulta;
	private List<RapServidorConselhoVO> listaProfissionais = new LinkedList<RapServidorConselhoVO>();
	private boolean permManterProfissionalNascimento;
	
	private DadosNascimentoSelecionadoVO dadosNascimentoSelecionado;
	private EquipeVO equipe;
	private SalaCirurgicaVO salaCirurgica;
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class,
				"aghu-casca");
	}
	
	private void inicio(){
		setRapServidorConselhoVO(null);
		
		preparaLista();
		populaSuggestionEquipe();
		populaSuggestionSala();
	}
	
	/**
	 * Suggestion Servidores conselho
	 * 
	 * @param param
	 * @return
	 */
	public List<RapServidorConselhoVO> pesquisarServidoresConselhoPorSiglaCentroCusto(String param) {
		List<RapServidorConselhoVO> vo = null;

		try {
			
			String conselhoDescricao = conselho != null ? conselho.getDescricao() : null;
			String paramDescricao = param != null ? param : null;
			
			vo = emergenciaFacade.pesquisarServidoresPorSiglaConselhoNumeroNome(conselhoDescricao, paramDescricao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return  this.returnSGWithCount(vo,pesquisarServidoresConselhoPorSiglaCentroCustoCount(param));
	}

	/**
	 * Suggestion  Servidores conselho - COUNT
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(String param) {
		Integer count = 0;
		if (param != null) {
			try {
				String conselhoDescricao = conselho != null ? conselho.getDescricao() : null;
				String paramDescricao = param != null ? param : null;
				
				count = emergenciaFacade.pesquisarServidoresPorSiglaConselhoNumeroNomeCount(conselhoDescricao, paramDescricao);

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return count.longValue();
	}
	
	/**
	 * Botão ADICIONAR
	 */
	public void adicionarProfissionalNascimento() {
		if (getRapServidorConselhoVO() != null) {
				McoProfNascs profNascs = new McoProfNascs();
				McoProfNascsId id  = new McoProfNascsId();
				McoNascimentos nascimento = new McoNascimentos();
				id.setSerMatriculaNasc(getRapServidorConselhoVO().getMatricula());
				id.setSerVinCodigoNasc(getRapServidorConselhoVO().getVinCodigo());
				id.setNasGsoPacCodigo(getNasGsoPacCodigo());
				id.setNasGsoSeqp(getNasGsoSeqp());
				id.setNasSeqp(getNasSeqp());
				profNascs.setId(id);
				nascimento = this.emergenciaFacade.obterMcoNascimentosPorChavePrimaria(getNasSeqp(), getNasGsoPacCodigo(), getNasGsoSeqp());
				profNascs.setMcoNascimentos(nascimento);
				
				try {
					this.emergenciaFacade.gravarMcoProfNasc(profNascs);
					apresentarMsgNegocio(Severity.INFO,
							"MENSAGEM_SUCESSO_CADASTRO_PROFISSIONAL");
				} catch (ApplicationBusinessException e) {
					apresentarMsgNegocio(Severity.ERROR,e.getMessage());
				}
				
				this.conselho=null;
				
			inicio();
		}
 	}
	
	/**
	 * Excluir
	 */
	public void excluirProfissionalNascimento() {
		if (getRemoverRapServidorConselhoVO() != null) {
			try {
				McoProfNascsId id  = new McoProfNascsId();
				id.setSerMatriculaNasc(getRemoverRapServidorConselhoVO().getMatricula());
				id.setSerVinCodigoNasc(getRemoverRapServidorConselhoVO().getVinCodigo());
				id.setNasGsoPacCodigo(getNasGsoPacCodigo());
				id.setNasGsoSeqp(getNasGsoSeqp());
				id.setNasSeqp(getNasSeqp());
				this.emergenciaFacade.excluirMcoProfNasc(id);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_PROFISSIONAL");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			inicio();
		}
	}
	
	public void preparaLista(){
		try {
			listaProfissionais = this.emergenciaFacade.listarProfissionais(getNasGsoSeqp(), getNasGsoPacCodigo(), getNasSeqp());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void populaSuggestionEquipe() {
		Short eqpSeq = this.dadosNascimentoSelecionado.getMcoNascimento().getEqpSeq();
		try {
			if (eqpSeq != null) {
				this.setEquipe(this.emergenciaFacade.obterEquipePorId(eqpSeq));
				
			} else {
				this.setEquipe(this.emergenciaFacade.buscarEquipeAssociadaLaudoAih(this.numeroConsulta, this.nasGsoPacCodigo));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void populaSuggestionSala() {
		Short unfSeq = this.dadosNascimentoSelecionado.getMcoNascimento().getSciUnfSeq();
		Short seqp = this.dadosNascimentoSelecionado.getMcoNascimento().getSciSeqp();
		
		if (unfSeq != null && seqp != null) {
			try {
				this.setSalaCirurgica(this.emergenciaFacade.obterDadosSalaCirurgica(unfSeq, seqp));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}else{
			this.setSalaCirurgica(null);
		}
	}
	
	public void atualizarVOEquipe() {
		houveAlteracao();
		if (this.equipe != null) {
			this.dadosNascimentoSelecionado.getMcoNascimento().setEqpSeq(this.equipe.getSeq().shortValue());
			
		} else {
			this.dadosNascimentoSelecionado.getMcoNascimento().setEqpSeq(null);
		}
	}
	
	public void atualizarVOSala() {
		houveAlteracao();
		if (this.salaCirurgica != null) {
			this.dadosNascimentoSelecionado.getMcoNascimento().setSciUnfSeq(this.salaCirurgica.getUnfSeq());
			this.dadosNascimentoSelecionado.getMcoNascimento().setSciSeqp(this.salaCirurgica.getSeqp());
			
		} else {
			this.dadosNascimentoSelecionado.getMcoNascimento().setSciUnfSeq(null);
			this.dadosNascimentoSelecionado.getMcoNascimento().setSciSeqp(null);
		}
	}
	
	public void houveAlteracao() {
		this.registrarGestacaoAbaNascimentoController.setHouveAlteracao(Boolean.TRUE);
	}
	
	public void preparaTela(Short nasGsoSeqp, Integer nasGsoPacCodigo, Integer nasSeqp, Integer numeroConsulta,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) {
		this.nasGsoPacCodigo = nasGsoPacCodigo;
		this.nasGsoSeqp = nasGsoSeqp;
		this.nasSeqp =	nasSeqp;
		this.numeroConsulta = numeroConsulta;
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
		
		setPermManterProfissionalNascimento(getPermissionService()
				.usuarioTemPermissao(obterLoginUsuarioLogado(), "manterProfissionalNascimento", "gravar"));
		
		inicio();
	}
	
	public List<EquipeVO> pesquisarEquipeAtivaCO(String param) {
		List<EquipeVO> listaRetorno = null;
		try {
			listaRetorno = this.emergenciaFacade.pesquisarEquipeAtivaCO(param);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return  this.returnSGWithCount(listaRetorno,pesquisarEquipeAtivaCOCount(param));
	}

	public Long pesquisarEquipeAtivaCOCount(String param) {
		Long retorno = null;
		try {
			retorno = this.emergenciaFacade.pesquisarEquipeAtivaCOCount(param);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}
	
	public List<SalaCirurgicaVO> obterSalasCirurgicasAtivasPorUnfSeqNome(String param) {
		List<SalaCirurgicaVO> listaRetorno = null;
		try {
			listaRetorno = this.emergenciaFacade.obterSalasCirurgicasAtivasPorUnfSeqNome(param);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return  this.returnSGWithCount(listaRetorno,obterSalasCirurgicasAtivasPorUnfSeqNomeCount(param));
	}

	public Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(String param) {
		Long retorno = null;
		try {
			retorno = this.emergenciaFacade.obterSalasCirurgicasAtivasPorUnfSeqNomeCount(param);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}

	public List<RapServidorConselhoVO> getListaProfissionais() {
		return listaProfissionais;
	}

	public void setListaProfissionais(List<RapServidorConselhoVO> listaProfissionais) {
		this.listaProfissionais = listaProfissionais;
	}
	
	public Integer getNasGsoPacCodigo() {
		return this.nasGsoPacCodigo;
	}

	public void setNasGsoPacCodigo(Integer nasGsoPacCodigo) {
		this.nasGsoPacCodigo = nasGsoPacCodigo;
	}

	public Short getNasGsoSeqp() {
		return this.nasGsoSeqp;
	}

	public void setNasGsoSeqp(Short nasGsoSeqp) {
		this.nasGsoSeqp = nasGsoSeqp;
	}

	public Integer getNasSeqp() {
		return this.nasSeqp;
	}

	public void setNasSeqp(Integer nasSeqp) {
		this.nasSeqp = nasSeqp;
	}

	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}

	public RapServidorConselhoVO getRapServidorConselhoVO() {
		return rapServidorConselhoVO;
	}

	public void setRapServidorConselhoVO(RapServidorConselhoVO rapServidorConselhoVO) {
		this.rapServidorConselhoVO = rapServidorConselhoVO;
	}
	
	public DominioConselho getConselho() {
		return conselho;
	}

	public void setConselho(DominioConselho conselho) {
		this.conselho = conselho;
	}

	public RapServidorConselhoVO getRemoverRapServidorConselhoVO() {
		return removerRapServidorConselhoVO;
	}

	public void setRemoverRapServidorConselhoVO(
			RapServidorConselhoVO removerRapServidorConselhoVO) {
		this.removerRapServidorConselhoVO = removerRapServidorConselhoVO;
	}

	public boolean isPermManterProfissionalNascimento() {
		return permManterProfissionalNascimento;
	}

	public void setPermManterProfissionalNascimento(
			boolean permManterProfissionalNascimento) {
		this.permManterProfissionalNascimento = permManterProfissionalNascimento;
	}
	
	public DadosNascimentoSelecionadoVO getDadosNascimentoSelecionado() {
		return dadosNascimentoSelecionado;
	}

	public void setDadosNascimentoSelecionado(
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) {
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
	}

	public EquipeVO getEquipe() {
		return equipe;
	}

	public void setEquipe(EquipeVO equipe) {
		this.equipe = equipe;
	}

	public SalaCirurgicaVO getSalaCirurgica() {
		return salaCirurgica;
	}

	public void setSalaCirurgica(SalaCirurgicaVO salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}
}
