package br.gov.mec.aghu.paciente.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jfree.chart.ChartUtilities;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCoombs;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.AipRegSanguineosId;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.vo.ComponenteSanguineo;
import br.gov.mec.aghu.paciente.vo.DadosSanguineos;
import br.gov.mec.aghu.paciente.vo.Medicamento;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.paciente.vo.PacienteProntuarioConsulta;
import br.gov.mec.aghu.paciente.vo.ProcedimentoReanimacao;
import br.gov.mec.aghu.vo.ProcedimentoReanimacaoVO;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class PacienteServiceImpl implements IPacienteService {

	private static final String PARAMETRO_OBRIGATORIO = "Parâmetro obrigatório";

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@Inject
	MessagesUtils messagesUtils;
	
	/**
	 * Web Service #35606
	 */
	@Override
	public Paciente obterDadosPacientePelaConsulta(
			Integer numeroConsulta) throws ServiceException, ApplicationBusinessException {

		AipPacientes pac = pacienteFacade.obterPacientePelaConsulta(numeroConsulta);
		if(pac == null) {
			return null;
		}
		
		Paciente paciente = new Paciente();
		paciente.setPeso(pacienteFacade.obterPesoPacienteUltimaHoraPelaConsulta(numeroConsulta));
		paciente.setAltura(pacienteFacade.obterAlturaPacienteUltimaHoraPelaConsulta(numeroConsulta));
		paciente.setIdade(pac.getIdadeFormat());
		
		return paciente;
	}

	/**
	 * Web Service #35604
	 */
	@Override
	public Paciente obterDadosContatoPacientePeloCodigo(
			Integer pacCodigo) throws ServiceException, ApplicationBusinessException {

		AipPacientes pac = pacienteFacade.obterPaciente(pacCodigo);
		if(pac == null) {
			return null;
		}
		
		VAipEnderecoPaciente endereco = cadastroPacienteFacade.obterEndecoPacienteSemValidacaoPermissao(pacCodigo);
		Paciente paciente = new Paciente();
		paciente.setNome(pac.getNome());
		paciente.setTelefone(pac.getFoneResidencial());
		paciente.setDddTelefone(pac.getDddFoneResidencial());
		if(endereco != null) {
			paciente.setLogradouro(endereco.getLogradouro());
			paciente.setNroLogradouro(endereco.getNroLogradouro());
			paciente.setComplLogradouro(endereco.getComplLogradouro());
			paciente.setBairro(endereco.getBairro());
			paciente.setCidade(endereco.getCidade());
			paciente.setUnidadeFederacao(endereco.getUf());
			paciente.setCep(endereco.getCep());
		}
		
		return paciente;
	}

	/**
	 * Web Service #34365,  Web Service #34372 e Web Service #42016  
	 */
	@Override
	public Paciente obterPacientePorCodigoOuProntuario(
			PacienteFiltro filtro) throws ServiceException, ApplicationBusinessException {
		
		AipPacientes paciente = this.pacienteFacade
				.obterPacientePorCodigoOuProntuario(filtro.getProntuario(), filtro.getCodigo(), null);
		
		if (paciente != null) {
			// transforma para o tipo de retorno
			Paciente result = new Paciente();
			result.setProntuario(paciente.getProntuario());
			result.setCodigo(paciente.getCodigo());
			result.setNome(paciente.getNome());
			result.setNomeMae(paciente.getNomeMae());
			result.setDtNascimento(paciente.getDtNascimento());
			result.setDtObito(paciente.getDtObito());
			result.setCor((paciente.getCor() != null) ? paciente.getCor().toString() : null);
			result.setSexo((paciente.getSexo() != null) ? paciente.getSexo().toString() : null);
			return result;
		}
		return null;
	}
	
	/**
	 * Web Service #34366
	 */
	@Override
	public List<Paciente> pesquisarPorFonemas(
			PacienteFiltro filtro) throws ServiceException, ApplicationBusinessException {
		
		List<AipPacientes> pacientes =  this.pacienteFacade
				.pesquisarPorFonemas(filtro.getFirstResult(), filtro.getMaxResults(), filtro.getNome(),
						filtro.getNomeMae(), filtro.getRespeitarOrdem(), filtro.getDtNascimento(), null);
		
		// transforma para o tipo de retorno
		List<Paciente> listaRetorno = new ArrayList<Paciente>();
		for (AipPacientes paciente : pacientes) {
			Paciente pac = new Paciente();
			pac.setProntuario(paciente.getProntuario());
			pac.setCodigo(paciente.getCodigo());
			pac.setNome(paciente.getNome());
			pac.setNomeMae(paciente.getNomeMae());
			pac.setDtNascimento(paciente.getDtNascimento());
			pac.setDtObito(paciente.getDtObito());
			
			listaRetorno.add(pac);
		}
		
		return listaRetorno;
	}
	
	/**
	 * Web Service #34366
	 */
	@Override
	public Long pesquisarPorFonemasCount(
			PacienteFiltro filtro) throws ServiceException, ApplicationBusinessException {
		return this.pacienteFacade
				.pesquisarPorFonemasCount(filtro.getNome(), filtro.getNomeMae(), filtro.getRespeitarOrdem(),
						filtro.getDtNascimento(), null);
	}
	
	@Override
	public String gerarEtiquetaPulseira(Integer pacCodigo, Integer atdSeq)
			throws ServiceException, ApplicationBusinessException {
		
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
		
		return this.pacienteFacade
				.gerarEtiquetaPulseira(paciente, atdSeq);
	}
	
	/**
	 * Web Service #34363
	 */
	@Override
	public Integer gerarNumeroProntuarioVirtualPacienteEmergencia(Integer pacCodigo, String nomeMicrocomputador)
			throws ServiceException, ApplicationBusinessException {
		
		Integer prontuarioVirtual = null;
		
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
		
		prontuarioVirtual = this.cadastroPacienteFacade.gerarNumeroProntuarioVirtualPacienteEmergencia(paciente, nomeMicrocomputador);
		
		return prontuarioVirtual;
	}
	
	@Override
	public PacienteProntuarioConsulta obterDadosGestantePorProntuario(Integer nroProntuario) throws ServiceException {
		if (nroProntuario == null) {
			throw new ServiceException(PARAMETRO_OBRIGATORIO);
		}
		
		try {
			PacienteProntuarioConsulta pacienteProntuarioConsulta = null;
			AipPacientes paciente = pacienteFacade.obterDadosGestantePorProntuario(nroProntuario);
			
			
			if (paciente != null) {
				Integer conNumero = obterUltimaConsultaCO(paciente);
				if(conNumero != null) {
					pacienteProntuarioConsulta = new PacienteProntuarioConsulta();
					// - codigo - Código do Paciente
					pacienteProntuarioConsulta.setCodigo(paciente.getCodigo());
					// - nome - nome da Gestante
					pacienteProntuarioConsulta.setNome(paciente.getNome());
					// - dt_nascimento - Data de Nascimento do paciente
					pacienteProntuarioConsulta.setDtNascimento(paciente.getDtNascimento());
					pacienteProntuarioConsulta.setProntuario(paciente.getProntuario());
					//Obtem numero da ultima consulta do paciente no CO
					pacienteProntuarioConsulta.setConsulta(conNumero);
				} else {
					throw new ServiceException(messagesUtils.getResourceBundleValue("ERRO_PACIENTE_SEM_CONSULTA_CO"));
				}
				
			}
			return pacienteProntuarioConsulta;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		} catch (ApplicationBusinessException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private Integer obterUltimaConsultaCO(AipPacientes paciente)
			throws ApplicationBusinessException {
		List<ConstanteAghCaractUnidFuncionais> caracts = new ArrayList<ConstanteAghCaractUnidFuncionais>();
		caracts.add(ConstanteAghCaractUnidFuncionais.CO);
		caracts.add(ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA);
		Integer conNumero = this.aghuFacade.obterUltimaConsultaDaPaciente(paciente.getCodigo(), caracts);
		return conNumero;
	}
	
	@Override
	public PacienteProntuarioConsulta obterDadosGestantePorConsulta(Integer nroConsulta) throws ServiceException {
		if (nroConsulta == null) {
			throw new ServiceException(PARAMETRO_OBRIGATORIO);
		}
		try {
			PacienteProntuarioConsulta pacienteProntuarioConsulta = null;
			AghAtendimentos atendimento = aghuFacade.obterDadosGestantePorConsulta(nroConsulta);
			if (atendimento != null) {
				pacienteProntuarioConsulta = new PacienteProntuarioConsulta();
				// - con_numero - Número da Consulta
				pacienteProntuarioConsulta.setConsulta(atendimento.getConsulta().getNumero());
				// - codigo - Código do Paciente
				pacienteProntuarioConsulta.setCodigo(atendimento.getPaciente().getCodigo());
				// - nome - nome da Gestante
				pacienteProntuarioConsulta.setNome(atendimento.getPaciente().getNome());
				// - dt_nascimento - Data de Nascimento do paciente
				pacienteProntuarioConsulta.setDtNascimento(atendimento.getPaciente().getDtNascimento());
				// - prontuario - Prontuário do paciente
				pacienteProntuarioConsulta.setProntuario(atendimento.getProntuario());
			}
			return pacienteProntuarioConsulta;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public Integer obterDadosGestantePorGestacaoPaciente(Short gsoSeqp, Integer gsoPacCodigo) throws ServiceException {
		if (gsoSeqp == null && gsoPacCodigo == null) {
			throw new ServiceException("Ao menos um parâmetro é obrigatório");
		}
		try {
			return aghuFacade.obterDadosGestantePorGestacaoPaciente(gsoSeqp, gsoPacCodigo);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public Boolean existsRegSanguineosPorCodigoPaciente(Integer pacCodigo) throws ServiceException {
		if (pacCodigo == null) {
			throw new ServiceException(PARAMETRO_OBRIGATORIO);
		}
		try {
			return pacienteFacade.existsRegSanguineosPorCodigoPaciente(pacCodigo);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public DadosSanguineos obterRegSanguineosPorCodigoPaciente(Integer pacCodigo, Byte seqp) throws ServiceException {
		if (pacCodigo == null || seqp == null) {
			throw new ServiceException("Parâmetros obrigatórios");
		}
		try {
			AipRegSanguineos grupo = pacienteFacade.obterRegSanguineosPorCodigoPaciente(pacCodigo, seqp);
			if (grupo != null) {
				DadosSanguineos dadosSanguineos = new DadosSanguineos();
				dadosSanguineos.setCoombs(grupo.getCoombs() != null ? grupo.getCoombs().toString() : null);
				dadosSanguineos.setFatorRh(grupo.getFatorRh());
				dadosSanguineos.setGrupoSanguineo(grupo.getGrupoSanguineo());
				return dadosSanguineos;
			}
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void removerRegSanguineosSemDadosPorPaciente(Integer pacCodigo) throws ServiceException {
		if (pacCodigo == null) {
			throw new ServiceException(PARAMETRO_OBRIGATORIO);
		}
		try {
			List<AipRegSanguineos> dados = pacienteFacade.listarRegSanguineosSemDadosPorPaciente(pacCodigo);
			if (dados != null && !dados.isEmpty()) {
				for (AipRegSanguineos aipRegSanguineos : dados) {
					pacienteFacade.removerAipRegSanguineos(aipRegSanguineos);
				}
			}
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public Boolean existsRegSanguineosSemDadosPorPaciente(Integer pacCodigo) throws ServiceException {
		if (pacCodigo == null) {
			throw new ServiceException(PARAMETRO_OBRIGATORIO);
		}
		try {
			return pacienteFacade.existsRegSanguineosSemDadosPorPaciente(pacCodigo);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void atualizarRegSanguineo(Integer pacCodigo, Byte seqp, String grupoSanguineoMae, String fatorRhMae,
			String coombs) throws ServiceException {
		try {
			AipRegSanguineos aipRegSanguineos = pacienteFacade.obterRegSanguineosPorCodigoPaciente(pacCodigo, seqp);
			
			aipRegSanguineos.setGrupoSanguineo(grupoSanguineoMae);
			aipRegSanguineos.setFatorRh(fatorRhMae);
			if (coombs != null) {
				aipRegSanguineos.setCoombs(DominioCoombs.getInstance(coombs));
			}
			aipRegSanguineos.setCriadoEm(new Date());
			
			this.pacienteFacade.atualizarAipRegSanguineos(aipRegSanguineos);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public void inserirRegSanguineo(Integer pacCodigo, String grupoSanguineoMae, String fatorRhMae,
			String coombs, Integer serMatricula, Short serVinCodigo) throws ServiceException {
		try {
			AipRegSanguineos aipRegSanguineos = new AipRegSanguineos();
			
			Byte maxSeqp = this.pacienteFacade.buscaMaxSeqpAipRegSanguineos(pacCodigo);
			maxSeqp = maxSeqp == null ? 0 : maxSeqp;
			
			AipRegSanguineosId id = new AipRegSanguineosId();
			id.setPacCodigo(pacCodigo);
			id.setSeqp(++maxSeqp);
			id.setSerMatricula(serMatricula);
			id.setSerVinCodigo(serVinCodigo);
			aipRegSanguineos.setId(id);
			aipRegSanguineos.setCriadoEm(new Date());
			aipRegSanguineos.setGrupoSanguineo(grupoSanguineoMae);
			aipRegSanguineos.setFatorRh(fatorRhMae);
			if (coombs != null) {
				aipRegSanguineos.setCoombs(DominioCoombs.getInstance(coombs));
			}
			
			this.pacienteFacade.inserirAipRegSanguineos(aipRegSanguineos);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	/**
	 * 
	 * Web Service #41092
	 * Serviço verificar se servidor tem qualificação para acessar registros
	 *
	 */
	@Override
	public Boolean verificarAcaoQualificacaoMatricula(final String descricao) throws ServiceException {
		try {
			return this.pacienteFacade.verificarAcaoQualificacaoMatricula(descricao);
		} catch (ApplicationBusinessException e) {
			throw new ServiceException(e.getMessage());
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public void atualizarPacienteDthrNascimento(Integer pacCodigo, Date dthrNascimento) throws ServiceException {
		try {
			this.pacienteFacade.atualizarPacienteDthrNascimento(pacCodigo, dthrNascimento);
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public byte[] getGraficoFrequenciaCardiaca(Integer pacCodigo, Short gsoSeqp) throws ServiceBusinessException, ServiceException {

		byte[] result = null;
		try {
			BufferedImage frequenciaCardiaca = prontuarioOnlineFacade.getGraficoFrequenciaCardiacaFetalSumAssistParto(pacCodigo, gsoSeqp);
			result = ChartUtilities.encodeAsPNG(frequenciaCardiaca);
		} catch (ApplicationBusinessException abe) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(abe));
		} catch (IOException ioe) {
			throw new ServiceException("Erro ao gerar gráfico de frequencia cardiaca: " + ioe.getMessage());
		} catch (RuntimeException re) {
			throw new ServiceException("Erro ao gerar gráfico de frequencia cardiaca: " + re.getMessage());
		}

		return result;
	}

	@Override
	public byte[] getGraficoPartograma(Integer pacCodigo, Short gsoSeqp) throws ServiceBusinessException, ServiceException {

		byte[] result = null;
		try {
			BufferedImage partograma = prontuarioOnlineFacade.getGraficoPartogramaSumAssistParto(pacCodigo, gsoSeqp);
			result = ChartUtilities.encodeAsPNG(partograma);
		} catch (ApplicationBusinessException abe) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(abe));
		} catch (IOException ioe) {
			throw new ServiceException("Erro ao gerar gráfico de partograma: " + ioe.getMessage());
		} catch (RuntimeException re) {
			throw new ServiceException("Erro ao gerar gráfico de partograma: " + re.getMessage());
		}

		return result;
	}
	
	
	/**
	 * Web Service #42557 
	 * @param pacCodigo
	 * @return
	 * @throws ServiceBusinessException
	 * @throws ServiceException
	 */
	@Override
	public BigDecimal obterAlturasPaciente(Integer pacCodigo) throws ServiceException {
		try {
			AipAlturaPacientes aipAlturaPacientes = pacienteFacade.obterAlturasPaciente(pacCodigo, DominioMomento.N);
			if (aipAlturaPacientes != null) {
				return aipAlturaPacientes.getAltura();
			}
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	
	/**
	 * Web Service #42480
	 * @param pacCodigo
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public BigDecimal obterPesoPacientesPorCodigoPaciente(Integer pacCodigo) throws  ServiceException{
		try {
			BigDecimal peso = pacienteFacade.obterPesoPacientesPorCodigoPaciente(pacCodigo);
			if (peso != null) {
				return peso.multiply(BigDecimal.valueOf(1000));
			}
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}
		
	}
	

	/**
	 * Web Service #42206
	 */
	@Override
	public List<ComponenteSanguineo> listarComponentesSuggestion(Object objPesquisa) throws ServiceBusinessException, ServiceException{
		List<ComponenteSanguineo> listaRetorno = new ArrayList<ComponenteSanguineo>();
		List<AbsComponenteSanguineo> lista = pacienteFacade.listarComponentesSuggestion(objPesquisa);
		for (AbsComponenteSanguineo item : lista) {
			ComponenteSanguineo retorno = new ComponenteSanguineo();
			retorno.setCodigo(item.getCodigo());
			retorno.setDescricao(item.getDescricao());
			listaRetorno.add(retorno);
		}
		return listaRetorno;
	}
	
	@Override
	public Long listarComponentesSuggestionCount(Object objPesquisa) throws ServiceBusinessException, ServiceException{
		return pacienteFacade.listarComponentesSuggestionCount(objPesquisa);
	}
	
	
	@Override
	public List<Medicamento> listarMedicamentosSuggestion(Object objPesquisa) throws ServiceBusinessException, ServiceException{
		List<AfaMedicamento> lista = pacienteFacade.listarMedicamentosSuggestion(objPesquisa);
		List<Medicamento> listaRetorno = new ArrayList<Medicamento>();
		for (AfaMedicamento afaMedicamento : lista) {
			Medicamento item = new Medicamento();
			item.setCodigo(afaMedicamento.getMatCodigo());
			item.setDescricao(afaMedicamento.getDescricao());
			listaRetorno.add(item);
		}
		return listaRetorno;
	}
	
	@Override
	public Long listarMedicamentosSuggestionCount(Object objPesquisa) throws ServiceBusinessException, ServiceException{
		return pacienteFacade.listarMedicamentosSuggestionCount(objPesquisa);
	}
	
	@Override
	public Medicamento obterMedicamentoPorId(Integer matCodigo){
		Medicamento retorno = new Medicamento();
		AfaMedicamento med = pacienteFacade.obterMedicamentoPorId(matCodigo);
		if(med == null){
			return null;	
		}
		retorno.setCodigo(med.getMatCodigo());
		retorno.setDescricao(med.getDescricao());
		return retorno;
	}

	@Override
	public ComponenteSanguineo obterComponentePorId(String codigo){
		ComponenteSanguineo retorno = new ComponenteSanguineo();
		AbsComponenteSanguineo comp = pacienteFacade.obterComponentePorId(codigo);
		if(comp == null){
			return null;	
		}
		retorno.setCodigo(comp.getCodigo());
		retorno.setDescricao(comp.getDescricao());
		return retorno;
	}
	
	@Override
	public ProcedimentoReanimacao obterProcReanimacao(Integer seq){
		ProcedimentoReanimacaoVO item = pacienteFacade.obterProcReanimacao(seq);
		 ProcedimentoReanimacao proc = new ProcedimentoReanimacao();
		 proc.setComponente(item.getComponente());
		 proc.setDescricao(item.getDescricao());
		 proc.setIndSituacao(item.getIndSituacao());
		 proc.setMedicamento(item.getMedicamento());
		 proc.setSeq(item.getSeq());
		 proc.setMatCodigo(item.getMatCodigo());
		 proc.setCompCodigo(item.getCompCodigo());
		 return proc;
	}
	
	@Override
	public void persistirSindrome(String descricao, String situacao) {
		pacienteFacade.persistirSindrome(descricao,situacao);
	}
	
	@Override
	public void ativarInativarSindrome(Integer seq) throws ServiceBusinessException, ServiceException{
		pacienteFacade.ativarInativarSindrome(seq);
	}
	
	@Override
	public void persistirBallard(Short seq,Short escore, Short igSemanas) {
		pacienteFacade.persistirBallard(seq,escore, igSemanas);
	}

	@Override
	public Integer obterUltimoAtdSeqRecemNascidoPorPacCodigo(Integer pacCodigo) {
		return aghuFacade.obterUltimoAtdSeqRecemNascidoPorPacCodigo(pacCodigo);
	}

	@Override
	public void atualizarAtendimentoGestante(Integer conNumero, Integer gsoPacCodigo, Short gsoSeqp, String nomeMicroComputador, Integer matricula,
			Short vinCodigo, Integer atdSeq) throws ServiceBusinessException, ServiceException {
		try {			
			this.pacienteFacade.atualizarAtendimentoGestante(gsoPacCodigo, gsoSeqp, nomeMicroComputador, atdSeq);			
		} catch (BaseException e) {
			throw new ServiceBusinessException(messagesUtils.getResourceBundleValue(e));
		} catch (RuntimeException e) {
			throw new ServiceException(e.getMessage());
		}		
	}

}