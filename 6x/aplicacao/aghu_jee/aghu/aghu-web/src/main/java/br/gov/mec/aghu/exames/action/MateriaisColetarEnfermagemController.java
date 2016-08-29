package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraItemExamesVO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemAmostraVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class MateriaisColetarEnfermagemController extends ActionController {

	private static final Log LOG = LogFactory.getLog(MateriaisColetarEnfermagemController.class);

	private static final long serialVersionUID = 7011123479689403423L;

	private AghUnidadesFuncionais unidadeFuncional;
	private Integer prontuarioPaciente;
	private Integer soeSeq;
	private Integer soeSeqSelecionado;
	private Short seqpSelecionado;
	private DominioSituacaoAmostra situacao;
	
	private AipPacientes paciente;
	
	private Boolean possuiCaracteristica;
	private Boolean desabilitaBotaoColetado;
	private Boolean desabilitaBotaoEmColeta;
	private Boolean desabilitaBotaoGerada;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IColetaExamesFacade coletaExamesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private AelAmostras amostraSelecionada;
	private List<MateriaisColetarEnfermagemAmostraVO> amostrasExames;
	private List<MateriaisColetarEnfermagemAmostraItemExamesVO> amostrasItemExames;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void pesquisar() {

		this.amostraSelecionada = null;
		this.soeSeqSelecionado = null;
		this.seqpSelecionado = null;
		
		this.amostrasExames = new ArrayList<MateriaisColetarEnfermagemAmostraVO>();
		this.amostrasItemExames = new ArrayList<MateriaisColetarEnfermagemAmostraItemExamesVO>();
		
		try {
			
			if (unidadeFuncional != null) {
				possuiCaracteristica = this.aghuFacade.unidadeFuncionalPossuiCaracteristica(unidadeFuncional.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_COLETA);
			}
			
			amostrasExames = examesFacade.pesquisarMateriaisColetaEnfermagemAmostra(unidadeFuncional, prontuarioPaciente, soeSeq, situacao);
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}

	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String param) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorUnidEmergencia(param, true);
	}
	
	public List<AipPacientes> pesquisarPacientes(String objPesquisa) {
		List<AipPacientes> pacientes = new ArrayList<AipPacientes>();
		String strPesquisa = String.valueOf(objPesquisa);
		if (StringUtils.isNotEmpty(strPesquisa)) {
			strPesquisa = strPesquisa.trim().replaceAll("/", "");
			Integer nroProntuario = Integer.valueOf(strPesquisa);
			AipPacientes pac = this.pacienteFacade
					.obterPacienteComAtendimentoPorProntuario(nroProntuario);
			if (pac != null) {
				pacientes.add(pac);
			}
		}
		return pacientes;
	}
	
	public void atualizarProntuario() {		
		if(getPaciente()!= null) {
			setProntuarioPaciente(getPaciente().getProntuario());
		} else {
			setProntuarioPaciente(null);
		}
	}
	
	public void limparPesquisa() {
		this.setUnidadeFuncional(null);
		this.setPaciente(null);
		this.setProntuarioPaciente(null);
		this.setSituacao(null);
		this.setSoeSeq(null);
		this.setSeqpSelecionado(null);
		this.setSoeSeqSelecionado(null);
		this.setAmostrasExames(null);
		this.setAmostrasItemExames(null);
		this.setAmostraSelecionada(null);
		this.setDesabilitaBotaoColetado(Boolean.TRUE);
		this.setDesabilitaBotaoEmColeta(Boolean.TRUE);
		this.setDesabilitaBotaoGerada(Boolean.TRUE);
	}
	
	public void obterItemExamesDaAmostra() {
		
		this.amostrasItemExames = new ArrayList<MateriaisColetarEnfermagemAmostraItemExamesVO>();
		
		amostrasItemExames = examesFacade.pesquisarMateriaisColetarEnfermagemPorAmostra(soeSeqSelecionado, seqpSelecionado);
		
		this.amostraSelecionada = this.examesFacade.buscarAmostrasPorId(this.soeSeqSelecionado, this.seqpSelecionado);
		
		if (!this.possuiCaracteristica) {
			this.setDesabilitaBotaoColetado(Boolean.TRUE);
			this.setDesabilitaBotaoEmColeta(Boolean.TRUE);
			this.setDesabilitaBotaoGerada(Boolean.TRUE);
		} else {
			if (this.coletaExamesFacade.verificaSituacaoAmostraGeradaOuEmColeta(amostraSelecionada.getSituacao())) {
				this.setDesabilitaBotaoColetado(Boolean.FALSE);
			} else {
				this.setDesabilitaBotaoColetado(Boolean.TRUE);
			}
			
			if (this.amostraSelecionada.getSituacao().equals(DominioSituacaoAmostra.M)
					|| this.amostraSelecionada.getSituacao().equals(DominioSituacaoAmostra.C)) {
				this.setDesabilitaBotaoEmColeta(Boolean.TRUE);
			} else {
				this.setDesabilitaBotaoEmColeta(Boolean.FALSE);
			}
			
			if (this.amostraSelecionada.getSituacao().equals(DominioSituacaoAmostra.G)) {
				this.setDesabilitaBotaoGerada(Boolean.TRUE);
			} else {
				this.setDesabilitaBotaoGerada(Boolean.FALSE);
			}
		}		
	}
	
	public void alterarSituacaoAmostraItem(DominioSituacaoAmostra amostraItemSituacao) {
		
		List<AelAmostraItemExames> amostraItens = this.examesFacade
				.buscarAelAmostraItemExamesPorAmostra(this.soeSeqSelecionado,
						Integer.valueOf(this.seqpSelecionado));
		
		String nomeMicrocomputador = null;
		
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try {

			for (AelAmostraItemExames item : amostraItens) {				
				item.setSituacao(amostraItemSituacao);				
				this.alterarAelAmostraItemExames(item, nomeMicrocomputador);
			}
			
			if(amostraItemSituacao.equals(DominioSituacaoAmostra.C)) {		
				this.examesFacade.imprimirEtiquetaAmostra(this.amostraSelecionada, this.unidadeFuncional, nomeMicrocomputador);				
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA", this.amostraSelecionada.getId().getSeqp());
			}
			
			this.amostrasExames = this.examesFacade
					.pesquisarMateriaisColetaEnfermagemAmostra(
							getUnidadeFuncional(), getProntuarioPaciente(),
							getSoeSeq(), getSituacao());
			
			this.obterItemExamesDaAmostra();
			
		} catch (BaseException ex) {		
			this.apresentarExcecaoNegocio(ex);
		} catch(Exception ex) {			
			this.apresentarMsgNegocio(Severity.ERROR, ex.getMessage());
		}
	}
	
	private void alterarAelAmostraItemExames(AelAmostraItemExames amostraItemExame, String nomeMicrocomputador) throws BaseException {
		this.examesFacade.persistirAelAmostraItemExames(amostraItemExame, true, nomeMicrocomputador);
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public DominioSituacaoAmostra getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAmostra situacao) {
		this.situacao = situacao;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Boolean getPossuiCaracteristica() {
		return possuiCaracteristica;
	}

	public void setPossuiCaracteristica(Boolean possuiCaracteristica) {
		this.possuiCaracteristica = possuiCaracteristica;
	}

	public Boolean getDesabilitaBotaoColetado() {
		return desabilitaBotaoColetado;
	}

	public void setDesabilitaBotaoColetado(Boolean desabilitaBotaoColetado) {
		this.desabilitaBotaoColetado = desabilitaBotaoColetado;
	}

	public Boolean getDesabilitaBotaoEmColeta() {
		return desabilitaBotaoEmColeta;
	}

	public void setDesabilitaBotaoEmColeta(Boolean desabilitaBotaoEmColeta) {
		this.desabilitaBotaoEmColeta = desabilitaBotaoEmColeta;
	}

	public Boolean getDesabilitaBotaoGerada() {
		return desabilitaBotaoGerada;
	}

	public void setDesabilitaBotaoGerada(Boolean desabilitaBotaoGerada) {
		this.desabilitaBotaoGerada = desabilitaBotaoGerada;
	}

	public Integer getSoeSeqSelecionado() {
		return soeSeqSelecionado;
	}

	public void setSoeSeqSelecionado(Integer soeSeqSelecionado) {
		this.soeSeqSelecionado = soeSeqSelecionado;
	}

	public Short getSeqpSelecionado() {
		return seqpSelecionado;
	}

	public void setSeqpSelecionado(Short seqpSelecionado) {
		this.seqpSelecionado = seqpSelecionado;
	}

	public AelAmostras getAmostraSelecionada() {
		return amostraSelecionada;
	}

	public void setAmostraSelecionada(AelAmostras amostraSelecionada) {
		this.amostraSelecionada = amostraSelecionada;
	}

	public List<MateriaisColetarEnfermagemAmostraVO> getAmostrasExames() {
		return amostrasExames;
	}

	public void setAmostrasExames(
			List<MateriaisColetarEnfermagemAmostraVO> amostrasExames) {
		this.amostrasExames = amostrasExames;
	}

	public List<MateriaisColetarEnfermagemAmostraItemExamesVO> getAmostrasItemExames() {
		return amostrasItemExames;
	}

	public void setAmostrasItemExames(
			List<MateriaisColetarEnfermagemAmostraItemExamesVO> amostrasItemExames) {
		this.amostrasItemExames = amostrasItemExames;
	}
}