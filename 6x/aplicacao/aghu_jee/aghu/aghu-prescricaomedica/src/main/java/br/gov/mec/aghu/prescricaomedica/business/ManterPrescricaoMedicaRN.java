package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamEstadoPacienteDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRegistroDAO;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendencia;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MamEstadoPaciente;
import br.gov.mec.aghu.model.MamEstadoPacienteMantido;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MamEstadoPacienteMantidoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MamTipoEstadoPacienteDAO;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.CursorPacVO;
import br.gov.mec.aghu.prescricaomedica.vo.EstadoPacienteVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class ManterPrescricaoMedicaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterPrescricaoMedicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;

	@Inject
	private MamEstadoPacienteDAO mamEstadoPacienteDAO;
	
	@Inject
	private MamEstadoPacienteMantidoDAO mamEstadoPacienteMantidoDAO; 
	
	@Inject
	private MamRegistroDAO mamRegistroDAO;

	@Inject
	private MamTipoEstadoPacienteDAO mamTipoEstadoPacienteDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -710394147769071464L;

	public static enum ManterPrescricaoMedicaRNExceptionCode implements
			BusinessExceptionCode {
		LOCAL_ATENDIMENTO_NAO_ENCONTRADO
	}

	/**
	 * Identifica o nome de cada chave declarada nos arquivos properties para o
	 * resumo do local de atendimento
	 */
	public static enum LocalAtendimento {
		LOCAL_ATENDIMENTO_LEITO, LOCAL_ATENDIMENTO_QUARTO, LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL;
	}

	public String buscarMensagemLocalizada(String chave, Object... parametros) {
		String mensagem = getResourceBundleValue(chave);

		// Faz a interpolacao de parâmetros na mensagem
		mensagem = java.text.MessageFormat.format(mensagem, parametros);

		return mensagem;
	}

	/**
	 * ORADB Function MPMC_LOCAL_PAC
	 * 
	 * @param aghAtendimentos
	 * @return Resumo indicando o local de atendimento do paciente.
	 * @throws ApplicationBusinessException
	 *             Lança exceção quando não for identificado o local de
	 *             atendimento do paciente.
	 */
	public String buscarResumoLocalPaciente(AghAtendimentos aghAtendimentos)
			throws ApplicationBusinessException {
		String local = null;
		aghAtendimentos = aghAtendimentoDAO.obterPorChavePrimaria(aghAtendimentos.getSeq());
		
		if(aghAtendimentos == null) {
			throw new IllegalArgumentException("Parâmetro Inválido!");
		}
		
		if (aghAtendimentos.getLeito() != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_LEITO.toString(),
					aghAtendimentos.getLeito().getLeitoID());
		} else if (aghAtendimentos.getQuarto() != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_QUARTO.toString(),
					aghAtendimentos.getQuarto().getDescricao());
		} else if (aghAtendimentos.getUnidadeFuncional() != null) {
			AghUnidadesFuncionais unidadeFuncional = aghAtendimentos.getUnidadeFuncional();
			local = buscarMensagemLocalizada(LocalAtendimento.LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL.toString(), unidadeFuncional.getAndarAlaDescricao());
		} else {
			// Código no AGH --> MPM-01237
			throw new ApplicationBusinessException(	ManterPrescricaoMedicaRNExceptionCode.LOCAL_ATENDIMENTO_NAO_ENCONTRADO);
		}
		return local;
	}
	
	public String buscarResumoLocalPacienteUniFuncional(AghAtendimentos atendimento) throws ApplicationBusinessException {
		String local = null;
		
		if(atendimento == null) {
			throw new IllegalArgumentException("Parâmetro Inválido!");
		}
	
		if (atendimento.getLeito() != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_LEITO.toString(),
					atendimento.getLeito().getLeitoID());
		} else 	if (atendimento.getQuarto() != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_QUARTO.toString(),
					atendimento.getQuarto().getDescricao());
		} else if (atendimento.getUnidadeFuncional() != null) {
			local = buscarMensagemLocalizada(LocalAtendimento.LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL.toString(), atendimento.getUnidadeFuncional().getAndarAlaDescricao());
		} else {
			throw new ApplicationBusinessException(ManterPrescricaoMedicaRNExceptionCode.LOCAL_ATENDIMENTO_NAO_ENCONTRADO);
		}
		return local;
	}
	
	public String buscarResumoLocalPacienteII(AghAtendimentos aghAtendimentos)
			throws ApplicationBusinessException {
		
		aghAtendimentos = getAghAtendimentoDAO().merge(aghAtendimentos);
		
		if(aghAtendimentos == null) {
			throw new IllegalArgumentException("Parâmetro Inválido!");
		}
		
		return obterResumoLocalPaciente(aghAtendimentos.getLeito(), aghAtendimentos.getQuarto(), aghAtendimentos
					.getUnidadeFuncional());
	}
	
	public String buscarResumoLocalPaciente(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional)
			throws ApplicationBusinessException {
		return obterResumoLocalPaciente(leito, quarto, unidadeFuncional);
	}
	
	public String buscarResumoLocalPaciente2(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional)
			throws ApplicationBusinessException {
		return obterResumoLocalPaciente2(leito, quarto, unidadeFuncional);
	}	

	/**
	 * Retorna array de objetos com dados do conselho profissional do servidor
	 * fornecido ou se null, do servidor logado.
	 * 
	 * ORADB Procedure MAMP_BUSCA_CONS_PROF
	 * 
	 * @return result[0] = servidor<br>
	 *         result[1] = nome<br>
	 *         result[2] = siglaConselho<br>
	 *         result[3] = regConselho<br>
	 * @throws ApplicationBusinessException  
	 */
	public Object[] buscaConsProf(Integer matricula,Short vincodigo) throws ApplicationBusinessException {
		RapServidores servidor = null;
		
		// retornos
		Object[] result = new Object[5];
		String nome = null;
		String siglaConselho = null;
		String regConselho = null;
		String cpf = null;
		// se não fornecido pega o logado
		if (matricula == null || vincodigo == null) {
			servidor = registroColaboradorFacade.obterServidorPorUsuario(this.obterLoginUsuarioLogado());
		}else{
			servidor = registroColaboradorFacade.obterServidor(vincodigo, matricula);
		}
		List<ConselhoProfissionalServidorVO> dadosConselhoServidor = this
				.getRegistroColaboradorFacade().buscaConselhosProfissionalServidorRegConselhoNotNull(
						servidor.getId().getMatricula(),
						servidor.getId().getVinCodigo(),
						DominioSituacao.A);
		if(servidor.getPessoaFisica().getCpf()!=null){
			cpf = servidor.getPessoaFisica().getCpf().toString();
		}

		nome = servidor.getPessoaFisica().getNome();		
		if (!dadosConselhoServidor.isEmpty()){
			ConselhoProfissionalServidorVO conselhoServidor = dadosConselhoServidor.get(0);
			siglaConselho = conselhoServidor.getSiglaConselho();
			regConselho = conselhoServidor.getNumeroRegistroConselho();
			if (regConselho != null) {
				if (servidor.getPessoaFisica().getSexo().equals(DominioSexo.M)
						&& conselhoServidor.getTituloMasculino() != null) {
					nome = conselhoServidor.getTituloMasculino() + " "
							+ StringUtils.substring(nome, 0, 40);
				} else if (servidor.getPessoaFisica().getSexo().equals(DominioSexo.F)
						&& conselhoServidor.getTituloFeminino() != null) {
					nome = conselhoServidor.getTituloFeminino() + " "
							+ StringUtils.substring(nome, 0, 40);
	
				}
			}
		}
		
	//	result[1] = servidor;
		result[0] = servidor;
		result[1] = nome;
		result[2] = siglaConselho; 
		result[3] = regConselho;
		result[4] = cpf;
		return result;
	}
	
	public Object[] buscaConsProf(RapServidores servidor) throws ApplicationBusinessException  {
		// retornos
		Object[] result = new Object[4];
		String nome = null;
		String siglaConselho = null;
		String regConselho = null;

		// se não fornecido pega o logado
		if (servidor == null || servidor.getId() == null) {
			servidor = this.servidorLogadoFacade.obterServidorLogado();
		}
		if (servidor == null || servidor.getId() == null) {
			return null;
		}
		List<ConselhoProfissionalServidorVO> dadosConselhoServidor = this
				.getRegistroColaboradorFacade().buscaConselhosProfissionalServidorRegConselhoNotNull(
						servidor.getId().getMatricula(),
						servidor.getId().getVinCodigo(),
						DominioSituacao.A);

		nome = servidor.getPessoaFisica().getNome();		
		if (!dadosConselhoServidor.isEmpty()){
			ConselhoProfissionalServidorVO conselhoServidor = dadosConselhoServidor.get(0);
			siglaConselho = conselhoServidor.getSiglaConselho();
			regConselho = conselhoServidor.getNumeroRegistroConselho();
			if (regConselho != null) {
				if (servidor.getPessoaFisica().getSexo().equals(DominioSexo.M)
						&& conselhoServidor.getTituloMasculino() != null) {
					nome = conselhoServidor.getTituloMasculino() + " "
							+ StringUtils.substring(nome, 0, 40);
				} else if (servidor.getPessoaFisica().getSexo().equals(DominioSexo.F)
						&& conselhoServidor.getTituloFeminino() != null) {
					nome = conselhoServidor.getTituloFeminino() + " "
							+ StringUtils.substring(nome, 0, 40);
	
				}
			}
		}
		result[0] = servidor;
		result[1] = nome;
		result[2] = siglaConselho;
		result[3] = regConselho;

		return result;
	}

	public String obterResumoLocalPaciente(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional)
			throws ApplicationBusinessException {
		String local = null;
	
		if (leito != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_LEITO.toString(),
					leito.getLeitoID());
		} else if (quarto != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_QUARTO.toString(),
					quarto.getDescricao());
		} else if (unidadeFuncional != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL
							.toString(), unidadeFuncional
							.getAndarAlaDescricao());
		} else {
			// CÃ³digo no AGH --> MPM-01237
			throw new ApplicationBusinessException(
					ManterPrescricaoMedicaRNExceptionCode.LOCAL_ATENDIMENTO_NAO_ENCONTRADO);
		}
		return local;
	}
	
	public String obterResumoLocalPaciente2(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional)
			throws ApplicationBusinessException {

		StringBuilder local = new StringBuilder();
		
		if (unidadeFuncional != null) {
			local.append(unidadeFuncional.getDescricao());
		}
		
		if (quarto != null) {
			local.append((unidadeFuncional != null) ? " - " : "");
			local.append(quarto.getDescricao());
		}
		
		if (leito != null) {
			local.append((quarto != null) ? " - " : "");
			local.append(leito.getLeitoID());
		} 

		return local.toString();
	}	
	
	public String obterResumoLocalPacienteSemLancarExcecao(AinLeitos leito, AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional) {
		String local = null;
		
		if (leito != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_LEITO.toString(),
					leito.getLeitoID());
		} else if (quarto != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_QUARTO.toString(),
					quarto.getDescricao());
		} else if (unidadeFuncional != null) {
			local = buscarMensagemLocalizada(
					LocalAtendimento.LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL
							.toString(), unidadeFuncional
							.getAndarAlaDescricao());
		} else {
			return null;
		}
		return local;
	}

	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected AghAtendimentoDAO getAghAtendimentoDAO(){
		return aghAtendimentoDAO;
	}
		
	/**
	 * #1378 - P1
	 * @throws ApplicationBusinessException
	 */
	public void alterarEstadoPaciente(EstadoPacienteVO estadoAnterior,  MamEstadoPaciente novoEstado, Long atdSeq) throws ApplicationBusinessException{

		if(novoEstado != null){
			if(estadoAnterior != null && estadoAnterior.getEsaSeq() != null){
				MamEstadoPaciente atualizarNovoEstado = mamEstadoPacienteDAO.obterPorChavePrimaria(estadoAnterior.getEsaSeq());
				atualizarNovoEstado.setDthrMvto(new Date());
				atualizarNovoEstado.setServMvto(this.servidorLogadoFacade.obterServidorLogado());
				atualizarNovoEstado.setDthrValidaMvto(new Date());
				atualizarNovoEstado.setServValidaMvto(this.servidorLogadoFacade.obterServidorLogado());
				atualizarNovoEstado.setIndPendencia(DominioIndPendencia.V);
				mamEstadoPacienteDAO.atualizar(atualizarNovoEstado);
}
		}

		MamEstadoPaciente ultimoEstadoSalvo = new MamEstadoPaciente();
		
		ultimoEstadoSalvo.setDthrCriacao(new Date());
		ultimoEstadoSalvo.setIndPendencia(DominioIndPendencia.P);
		ultimoEstadoSalvo.setMamTipoEstadoPaciente(mamTipoEstadoPacienteDAO.obterOriginal(novoEstado.getMamTipoEstadoPaciente()));
		ultimoEstadoSalvo.setServResponsavel(this.servidorLogadoFacade.obterServidorLogado());
		ultimoEstadoSalvo.setTrgSeq(novoEstado.getTrgSeq());
		ultimoEstadoSalvo.setMamRegistro(novoEstado.getMamRegistro());
		ultimoEstadoSalvo.setAghAtendimentos(novoEstado.getAghAtendimentos());
		ultimoEstadoSalvo.setPrescricaoMedica(novoEstado.getPrescricaoMedica());
		ultimoEstadoSalvo.setDthrValida(new Date());
		ultimoEstadoSalvo.setServValidador(this.servidorLogadoFacade.obterServidorLogado());
		ultimoEstadoSalvo.setMamEstadoPaciente(null);
		mamEstadoPacienteDAO.persistir(ultimoEstadoSalvo);
		
		//P2
		gerenciarEstadoPaciente(ultimoEstadoSalvo.getSeq(), novoEstado.getMamRegistro().getSeq());
	
	}

	/**
	 * #1378
	 * @param estadoC2
	 * @param registro
	 */
	public void manterEstadoPaciente(MamEstadoPaciente estadoC2, Long registro) {
		MamEstadoPacienteMantido estadoMantidoIgual = new MamEstadoPacienteMantido();
		MamRegistro reg = mamRegistroDAO.obterPorChavePrimaria(registro);
		
		estadoMantidoIgual.setMamRegistro(reg);
		estadoMantidoIgual.setMamEstadoPaciente(estadoC2);
		estadoMantidoIgual.setCriadoEm(new Date());
		estadoMantidoIgual.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		mamEstadoPacienteMantidoDAO.persistir(estadoMantidoIgual);
	}
	
	/**
	 * #1378 - P2
	 * @ORADB
	 * @param estado
	 */
	public void gerenciarEstadoPaciente(Long esaSeq, Long rgtSeq){
		
		List<MamEstadoPaciente> curEsa = mamEstadoPacienteDAO.gerenciaEstadoPacientes(esaSeq, rgtSeq);
		
		for (MamEstadoPaciente estado : curEsa) {
			if (estado.getIndPendencia().equals(DominioIndPendencia.R)) {
				trataRascunho(estado);
			} else if (estado.getIndPendencia().equals(DominioIndPendencia.P)) {
				trataPendente(estado);
			} else if (estado.getIndPendencia().equals(DominioIndPendencia.E)) {
				trataExclusao(estado);
			}
		}
	}
	
	/**
	 * #1378
	 * @ORADB p_trata_rascunho 
	 */
	private void trataRascunho(MamEstadoPaciente estado) {
		MamEstadoPaciente aux = estado.getMamEstadoPaciente();
		Long esaSeq  = null;
		mamEstadoPacienteDAO.removerPorId(estado.getSeq());
		
		if(aux != null){
			esaSeq = aux.getSeq();
		}
		if(esaSeq != null){
			MamEstadoPaciente estadoAtualizado = mamEstadoPacienteDAO.obterPorChavePrimaria(esaSeq);
			if(estadoAtualizado != null){
				estadoAtualizado.setDthrMvto(null);
				estadoAtualizado.setServMvto(null);
				mamEstadoPacienteDAO.atualizar(estadoAtualizado);
			}
		}
	}

	/**
	 * #1378 
	 * @ORADB p_trata_pendente
	 */
	private void trataPendente(MamEstadoPaciente estado) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		estado.setIndPendencia(DominioIndPendencia.V);
		estado.setDthrValida(new Date());
		estado.setServValidador(servidorLogado);
		mamEstadoPacienteDAO.atualizar(estado);
		
		MamEstadoPaciente estadoAux = estado.getMamEstadoPaciente();
		
		if(estadoAux != null){
			estadoAux.setIndPendencia(DominioIndPendencia.V);
			estadoAux.setDthrValidaMvto(estado.getDthrValidaMvto());
			estadoAux.setServValidaMvto(servidorLogado);
			mamEstadoPacienteDAO.atualizar(estadoAux);
		}		
	}
	
	/**
	 * #1378
	 * @ORADB p_trata_exclusao
	 */
	private void trataExclusao(MamEstadoPaciente estado) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		estado.setIndPendencia(DominioIndPendencia.V);
		estado.setDthrValidaMvto(estado.getDthrMvto());
		estado.setServValidaMvto(servidorLogado);
		mamEstadoPacienteDAO.atualizar(estado);
	}
	
	/**
	 * C2
	 * @param trgSeq
	 * @return
	 */
	public EstadoPacienteVO obterEstadoPacientePeloTrgSeq(Long trgSeq){
		return mamEstadoPacienteDAO.obterEstadoPacientePeloTrgSeq(trgSeq);
	}
	
	/**
	 * C3
	 * @param atdSeq
	 * @return
	 */
	public EstadoPacienteVO obterEstadoPacientePeloAtdSeq(Long atdSeq){
		return mamEstadoPacienteDAO.obterEstadoPacientePeloAtdSeq(atdSeq);
	}
	
	/**
	 * #1378 - F3
	 * @ORADB FUNCTION c_traz_con_numero
	 */
	public Integer cTrazConNumero(Long pTrgSeq){
//		Short vSeqp;
		Integer vConNumero = null;
		MamTrgEncInterno curTei = prescricaoMedicaFacade.recuperarCurTei(pTrgSeq);
		if(curTei != null){
			vConNumero = curTei.getConsulta().getNumero();
		}
		return vConNumero;
	}
	
	/**
	 * #1378 - F4
	 * @ORADB MAMC_IDENTIFICA_PAC
	 */
	public String identificaPac(Integer pConNumero){
		
		CursorPacVO cursorPac = prescricaoMedicaFacade.buscarCursorPac(pConNumero);
		
		String vIdade;
		String vTexto = StringUtils.EMPTY;
		
		if(cursorPac != null){
//			Date cPac = prescricaoMedicaFacade.buscarDataNascimentoPaciente(cursorPac.getPacCodigo());
			if(cursorPac.getClcCodigo() == 7 || cursorPac.getIndEspPediatrica()){
				vIdade = editarIdade(cursorPac.getPacCodigo());
			} else {
				vIdade = calcularIdadeAnos(cursorPac.getPacCodigo());
				
				if(Integer.valueOf(vIdade) == 1){
					vIdade = vIdade.concat(" ano");
				} else if(Integer.valueOf(vIdade) > 1 && Integer.valueOf(vIdade) < 12){
					vIdade = editarIdade(cursorPac.getPacCodigo());	
				} else {
					vIdade = vIdade.concat(" anos");
				}
			}
			
			vTexto = cursorPac.getNome().concat(", ").concat(vIdade); 
			
			if(cursorPac.getProntuario() != null){
				vTexto = vTexto.concat(" - Prontuário: ").concat(cursorPac.getProntuario().toString());
			}
		}
		
		return vTexto;
	}

	/**
	 * #1378 - F6
	 * @ORADB MAMC_CALC_IDADE_ANOS
	 * @param pPacCodigo
	 * @author marcelo.deus
	 */
	private String calcularIdadeAnos(Integer pPacCodigo){
		
		Date cPac = prescricaoMedicaFacade.buscarDataNascimentoPaciente(pPacCodigo);
		
		if(cPac == null){
			return "0";
		} else {
			return DateUtil.getIdade(cPac).toString();
		}
	}
	
	/**
	 * #1378 - F5
	 * @ORADB MAMC_EDITA_IDD_3_PAC
	 * @param pPacCodigo
	 * @author marcelo.deus
	 */
	private String editarIdade(Integer pPacCodigo){

		String vRetorno = StringUtils.EMPTY;
		Date cPac = prescricaoMedicaFacade.buscarDataNascimentoPaciente(pPacCodigo);
		
		if(cPac != null){
			vRetorno = DateUtil.obterIdadeFormatadaAnoMesDia(cPac);
		}
		return vRetorno;
	}
	
	/**
	 * Obter informação do serviço e médico do atendimento infornmado
	 * #50931 - P1
	 * @ORADB mamc_int_servico_eqp
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterServicoMedicoDoAtendimento(Integer atdSeq) throws ApplicationBusinessException{
		String texto = null;
		
		Object[] dadosServico = aghAtendimentoDAO.obterServicoMedicoDoAtendimento(atdSeq);
		if (dadosServico != null){
			texto =  StringUtils.left(ambulatorioFacade.mpmcMinusculo((String)dadosServico[0], 2), 40);

			if (dadosServico[1] != null){
				Object[] responsavel = this.buscaConsProf((int)dadosServico[1], (short)dadosServico[2]);
				texto = texto.concat(", Equipe: ").concat(StringUtils.left(ambulatorioFacade.mpmcMinusculo((String)responsavel[1], 2), 40));
			}
		}
		
		return texto;
	}
}
