package br.gov.mec.aghu.exames.questionario.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemQuestionario;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelExamesQuestionarioDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelRespostaQuestaoDAO;
import br.gov.mec.aghu.exames.dao.VAelItemSolicExamesDAO;
import br.gov.mec.aghu.exames.dao.VAelSolicAtendsDAO;
import br.gov.mec.aghu.exames.questionario.vo.InformacaoComplementarVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.model.AelExQuestionarioOrigens;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelRespostaQuestaoId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class InformacaoComplementarON extends BaseBusiness {


@EJB
private InformacaoComplementarRN informacaoComplementarRN;

private static final Log LOG = LogFactory.getLog(InformacaoComplementarON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private VAelItemSolicExamesDAO vAelItemSolicExamesDAO;

@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelExamesQuestionarioDAO aelExamesQuestionarioDAO;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@Inject
private AelRespostaQuestaoDAO aelRespostaQuestaoDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IPacienteFacade pacienteFacade;

@Inject
private VAelSolicAtendsDAO vAelSolicAtendsDAO;

@EJB
private ICadastroPacienteFacade cadastroPacienteFacade;

@EJB
private IExamesFacade examesFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6321084992083274280L;
	
	public enum InformacaoComplementarONExceptionCode implements BusinessExceptionCode {
		AEL_01360;
	}
	

	public List<InformacaoComplementarVO> pesquisarInformacoesComplementares(Integer pacCodigo, Integer soeSeq, Short seqp, Integer qtnSeq) throws ApplicationBusinessException {
		List<InformacaoComplementarVO> lista =  getVAelItemSolicExamesDAO().pesquisarInformacoesComplementares(pacCodigo, soeSeq, seqp, qtnSeq);
		for(InformacaoComplementarVO informacaoComplementarVO:lista){
			informacaoComplementarVO.setSolicitacao(informacaoComplementarVO.getSoeSeq()+"-"+informacaoComplementarVO.getSeqp());
			
			AipPacientes paciente = this.getPacienteFacade().obterPacientePorCodigo(pacCodigo);
			informacaoComplementarVO = this.popularDadosPaciente(informacaoComplementarVO, paciente);
			List<VAelSolicAtendsVO> listaVOs = this.pesquisarSolicitacaoPorSoePacienteUnf(informacaoComplementarVO.getSoeSeq(), paciente.getCodigo(), informacaoComplementarVO.getUnfSeq());
			VAelSolicAtendsVO vAelSolicAtendsVO = listaVOs.get(0);
			AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(vAelSolicAtendsVO.getAtdSeq());
			String local = this.getPrescricaoMedicaFacade().buscarResumoLocalPaciente(atendimento);
			informacaoComplementarVO.setLocal(local);
			informacaoComplementarVO.setData(vAelSolicAtendsVO.getDataSolicitacao());
			informacaoComplementarVO.setNomeServidor(this.getInformacaoComplementarRN().obterNomeProfissionalConselho(informacaoComplementarVO.getMatricula(), informacaoComplementarVO.getVinCodigo()));
			informacaoComplementarVO.setPrioridadeExecucao1("Supervisão "+this.getInformacaoComplementarRN().obterConvenioAtendimento(informacaoComplementarVO.getAtdSeq()));
			StringBuilder prioridadeExecucao2 = new StringBuilder(); 
			prioridadeExecucao2.append("Solicitação de ")
			.append(informacaoComplementarVO.getUnf2Descricao());
			if(informacaoComplementarVO.getDescricaoExame()!=null){
				prioridadeExecucao2.append('/');
				prioridadeExecucao2.append(informacaoComplementarVO.getDescricaoExame());
			} 
			informacaoComplementarVO.setPrioridadeExecucao2(prioridadeExecucao2.toString());
			
			if(informacaoComplementarVO.getVinCodigo()!=null && informacaoComplementarVO.getMatricula()!=null){
				RapServidoresId id = new RapServidoresId();
				id.setVinCodigo(informacaoComplementarVO.getVinCodigo());
				id.setMatricula(informacaoComplementarVO.getMatricula());
				RapServidores servidor = this.getRegistroColaboradorFacade().buscaServidor(id);
				if(servidor!=null){
					RapPessoasFisicas pessoaFisica = servidor.getPessoaFisica();
					if(pessoaFisica!=null){
						informacaoComplementarVO.setCpfMedico(pessoaFisica.getCpf());
						if(informacaoComplementarVO.getCpfMedico()!=null){
							informacaoComplementarVO.setDescricaoCpfMedico(informacaoComplementarVO.getCpfMedico().toString());
						} else {
							informacaoComplementarVO.setDescricaoCpfMedico("0");
						}
					}		
				}
			}
			
			
			Long codigoSus = this.getInformacaoComplementarRN().obterCodigoSus(informacaoComplementarVO.getSigla(), informacaoComplementarVO.getManSeq(), informacaoComplementarVO.getGrupoConvenio(), vAelSolicAtendsVO.getOrigem());
			if(codigoSus != null) {
				informacaoComplementarVO.setCodigoProcedimento(codigoSus.toString());
			}
			if (paciente.getCor() != null) {
				informacaoComplementarVO.setRaca(paciente.getCor().getDescricao());
			}
			if(paciente.getIdade()<18){
				informacaoComplementarVO.setResponsavel(this.getAmbulatorioFacade().mpmcMinusculo(paciente.getNomeMae(), 2));
			} else {
				informacaoComplementarVO.setResponsavel(this.getAmbulatorioFacade().mpmcMinusculo(paciente.getNome(), 2));
			}

			if(paciente.getGrauInstrucao() != null) {
				informacaoComplementarVO.setGrauInstrucao(paciente.getGrauInstrucao().getDescricao());
			}
			AelRespostaQuestaoId id = new AelRespostaQuestaoId();
			id.setEqeEmaExaSigla(informacaoComplementarVO.getSigla());
			id.setEqeEmaManSeq(informacaoComplementarVO.getManSeq());
			id.setEqeQtnSeq(informacaoComplementarVO.getQtnSeq1());
			id.setIseSeqp(informacaoComplementarVO.getSeqp());
			id.setIseSoeSeq(informacaoComplementarVO.getSoeSeq());
			id.setQquQaoSeq(informacaoComplementarVO.getQaoSeq());
			id.setQquQtnSeq(informacaoComplementarVO.getQtnSeq());
			AelRespostaQuestao respostaQuestao = this.getAelRespostaQuestaoDAO().obterPorChavePrimaria(id);
			if(respostaQuestao!=null){
				informacaoComplementarVO.setResposta(respostaQuestao.getResposta());
			}
		}
		return lista;
	}
	
	private InformacaoComplementarVO popularDadosPaciente(InformacaoComplementarVO informacaoComplementarVO, AipPacientes paciente){
		informacaoComplementarVO.setIdade(paciente.getIdade());
		if(paciente.getCpf() != null) {
			informacaoComplementarVO.setDescricaoCpf(CoreUtil.formataCPF(paciente.getCpf()));
		}
		
		if(paciente.getDddFoneResidencial() != null && paciente.getFoneResidencial() != null) {
			String foneResidencialComDDD = paciente.getDddFoneResidencial().toString() +
					"-" + paciente.getFoneResidencial().toString();
			informacaoComplementarVO.setFoneResidencialPacComDDD(foneResidencialComDDD);
		}
		
		if(paciente.getDddFoneRecado() != null && paciente.getFoneRecado() != null) {
			String foneRecadoComDDD = paciente.getDddFoneRecado().toString() + "-" + paciente.getFoneRecado();
			informacaoComplementarVO.setFoneRecadoPacComDDD(foneRecadoComDDD);
		}
		
		if(paciente.getAipCidades()!=null){
			informacaoComplementarVO.setNaturalidade(paciente.getAipCidades().getNome());
		}
		VAipEnderecoPaciente enderecoPaciente = this.getCadastroPacienteFacade().obterEndecoPaciente(paciente.getCodigo());
		if(enderecoPaciente!=null){
			informacaoComplementarVO.setCep(enderecoPaciente.getCep());
			informacaoComplementarVO.setBairro(enderecoPaciente.getBairro());
			if(enderecoPaciente.getComplLogradouro()!=null){
				informacaoComplementarVO.setLogradouro(enderecoPaciente.getLogradouro()+" "+
						enderecoPaciente.getNroLogradouro()+"/"+enderecoPaciente.getComplLogradouro());	
			} else {
				informacaoComplementarVO.setLogradouro(enderecoPaciente.getLogradouro()+" "+
						enderecoPaciente.getNroLogradouro());
			}
			String codIbge = enderecoPaciente.getCodIbge().toString().substring(0,6);
			String endereco = enderecoPaciente.getCidade()+" Cod IBGE: "+ codIbge +" Estado: "+enderecoPaciente.getUf()+" CEP: "+enderecoPaciente.getCep().toString();
			informacaoComplementarVO.setEndereco(endereco);
		}
		return informacaoComplementarVO;
	}
	
	public List<VAelSolicAtendsVO> pesquisarSolicitacaoPorSoePacienteUnf(Integer soeSeq, Integer pacCodigo, Short unfSeq){
		List<Object[]> resultadoPesquisa = this.getVAelSolicAtendsDAO().pesquisarSolicitacaoPorSoePacienteUnf(soeSeq, pacCodigo, unfSeq);
		List<VAelSolicAtendsVO> listaSolicitacoes = new ArrayList<VAelSolicAtendsVO>();
		
		if(resultadoPesquisa!=null && !resultadoPesquisa.isEmpty()) {
			for(Object[] item : resultadoPesquisa) {
				VAelSolicAtendsVO solicitacaoVO = new VAelSolicAtendsVO();
				solicitacaoVO.setNumero((Integer)item[0]); //soeSeq
				solicitacaoVO.setCspCnvCodigo((Short)item[1]); 
				solicitacaoVO.setCspSeq((Short)item[2]); 
				solicitacaoVO.setOrigem(DominioOrigemAtendimento.getInstance((String)item[3]));
				solicitacaoVO.setCodPaciente((Integer)item[4]);
				solicitacaoVO.setProntuario((Integer)item[5]);
				solicitacaoVO.setTipo((String)item[6]);
				solicitacaoVO.setUnfDescricao((String)item[7]);
				if(item[8]!=null){
					solicitacaoVO.setQuarto(item[8].toString());	
				}
				solicitacaoVO.setLeito((String)item[9]);
				solicitacaoVO.setInformacoesClinicas((String)item[10]);	
				solicitacaoVO.setAtdSeq((Integer)item[11]);	
				solicitacaoVO.setDataSolicitacao((Date)item[12]);	
				listaSolicitacoes.add(solicitacaoVO);
			}
		}
		return listaSolicitacoes;
	}
	
	public void validarEnderecoPaciente(Integer pacCodigo) throws ApplicationBusinessException {
		VAipEnderecoPaciente endereco = getCadastroPacienteFacade().obterEndecoPaciente(pacCodigo);
		if(endereco == null) {
			throw new ApplicationBusinessException(InformacaoComplementarONExceptionCode.AEL_01360);
		}
	}
	
	/**
	 * #5859 - ON1
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @param qtnSeq
	 * @return numero de vias
	 */
	public Byte obterNumeroViasImpressaoInformacoesComplementares(Integer soeSeq, Short seqp, Integer qtnSeq) {
		AelItemSolicitacaoExames itemSol = getAelItemSolicitacaoExameDAO().obterPorId(soeSeq, seqp);
		
		String emaExaSigla = itemSol.getAelExameMaterialAnalise().getId().getExaSigla();
		Integer emaManSeq = itemSol.getAelExameMaterialAnalise().getId().getManSeq();
		
		AelExamesQuestionario exameQuest = getAelExamesQuestionarioDAO().obterAelExamesQuestionario(emaExaSigla, emaManSeq, qtnSeq);
		
		if(itemSol.getSolicitacaoExame().getAtendimento() != null) {
			Set<AelExQuestionarioOrigens> origens = exameQuest.getAelExQuestionarioOrigens();
			if(origens != null && origens.size() > 0) {
				for(AelExQuestionarioOrigens origem : origens) {
					origem.getId().getOrigemQuestionario();
					if((DominioOrigemQuestionario.T.equals(origem.getId().getOrigemQuestionario())
							|| origem.getId().getOrigemQuestionario().name().equals(itemSol.getSolicitacaoExame().getAtendimento().getOrigem().name()))
							&& origem.getNroVias() != null && !origem.getNroVias().equals(0)) {
						return origem.getNroVias().byteValue();
					}
				}
			}
		}
		
		if(exameQuest.getNroVias() != null && !exameQuest.getNroVias().equals(0)) {
			return exameQuest.getNroVias().byteValue();
		} else {
			return exameQuest.getAelQuestionarios().getNroVias();
		}
	}
	
	protected VAelSolicAtendsDAO getVAelSolicAtendsDAO() {
		return vAelSolicAtendsDAO;
	}
	
	protected AelExamesQuestionarioDAO getAelExamesQuestionarioDAO() {
		return aelExamesQuestionarioDAO;
	}

	
	protected VAelItemSolicExamesDAO getVAelItemSolicExamesDAO() {
		return vAelItemSolicExamesDAO;
	}
	
	protected AelRespostaQuestaoDAO getAelRespostaQuestaoDAO() {
		return aelRespostaQuestaoDAO;
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected InformacaoComplementarRN getInformacaoComplementarRN() {
		return informacaoComplementarRN;
	}

	public Integer obterPacCodigoPelaSolicitacao(Integer soeSeq) {
		Integer pacCodigo = null;
		
		AelSolicitacaoExames solicitacao = getExamesFacade().obterAelSolicitacaoExamesPeloId(soeSeq);
		if(solicitacao.getAtendimento() != null && solicitacao.getAtendimento().getPaciente() != null) {
			pacCodigo = solicitacao.getAtendimento().getPaciente().getCodigo();
		} else if(solicitacao.getAtendimentoDiverso() != null && solicitacao.getAtendimentoDiverso().getAipPaciente() != null) {
			pacCodigo = solicitacao.getAtendimentoDiverso().getAipPaciente().getCodigo();
		}
		return pacCodigo;
	}
}