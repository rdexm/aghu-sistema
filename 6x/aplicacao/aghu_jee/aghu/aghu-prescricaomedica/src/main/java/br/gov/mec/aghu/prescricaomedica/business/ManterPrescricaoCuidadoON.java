package br.gov.mec.aghu.prescricaomedica.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoCuidadoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCuidadoUsualDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterPrescricaoCuidadoON extends BaseBusiness {

	@EJB
	private PrescricaoMedicaON prescricaoMedicaON;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;
	
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoCuidadoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmCuidadoUsualDAO mpmCuidadoUsualDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@Inject
	private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8598994877384178087L;

	public enum ManterPrescricaoCuidadoExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PRESCRICAO_CUIDADO_INVALIDO, MENSAGEM_CUIDADO_USUAL_INVALIDO, MENSAGEM_TIPO_FREQUENCIA_INVALIDO, MENSAGEM_TIPO_FREQUENCIA_INATIVO, MENSAGEM_SERVIDOR_INVALIDO, MENSAGEM_CUIDADO_USUAL_EXIGE_COMPLEMENTO, MENSAGEM_INFORME_FREQUENCIA_E_TIPO, MENSAGEM_TIPO_FREQUENCIA_EXIGE_INFORMACAO_FREQUENCIA, MENSAGEM_TIPO_FREQUENCIA_NAO_PERMITE_INFORMACAO
	}

	/**
	 * Retorna lista com todos os cuidados usuais ativos e que podem ser
	 * prescritos para a unidade onde o paciente está sendo atendido.
	 * 
	 * @param descricao
	 * @param unidade
	 * @return
	 */
	public List<MpmCuidadoUsual> getListaCuidadosUsuaisAtivosUnidade(
			Object descricao, AghUnidadesFuncionais unidade) {
		return this.getMpmCuidadoUsualDAO()
				.getListaCuidadosUsuaisAtivosUnidade(descricao, unidade);
	}
	
	/**
	 * Retorna o número de registros de cuidados usuais ativos e que podem ser
	 * prescritos para a unidade onde o paciente está sendo atendido.
	 *
	 * @param descricao
	 * @param unidade
	 * @return
	 */
	public Long getListaCuidadosUsuaisAtivosUnidadeCount(Object descricao,
			AghUnidadesFuncionais unidade) {
		return this.getMpmCuidadoUsualDAO()
				.getListaCuidadosUsuaisAtivosUnidadeCount(descricao, unidade);
	}

	public void incluir(MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
	throws BaseException {
		incluir(prescricaoCuidado, false, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public void incluir(MpmPrescricaoCuidado prescricaoCuidado, Boolean isCopiado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		if (prescricaoCuidado == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_PRESCRICAO_CUIDADO_INVALIDO);
		}

		// FK cuidadoUsual
		prescricaoCuidado.setMpmCuidadoUsuais(this
				.validaAssociacao(prescricaoCuidado.getMpmCuidadoUsuais()));

		// FK tipo Aprazamento
		prescricaoCuidado.setMpmTipoFreqAprazamentos(this
				.validaAssociacao(prescricaoCuidado
						.getMpmTipoFreqAprazamentos()));

		if (!isCopiado){
			this.validar(prescricaoCuidado);			
		}

		// inicializa os valores padrão
		prescricaoCuidado.setId(new MpmPrescricaoCuidadoId(prescricaoCuidado
				.getPrescricaoMedica().getId().getAtdSeq(), null));
		prescricaoCuidado.setServidor(this.validaAssociacao(prescricaoCuidado
				.getServidor()));
		prescricaoCuidado.setCriadoEm(new Date());
		
		// se IndPendente diferente de B=Modelo Básico atribui IndPendente		
		if  (!DominioIndPendenteItemPrescricao.B.equals(prescricaoCuidado.getIndPendente())) {
			prescricaoCuidado.setIndPendente(DominioIndPendenteItemPrescricao.P);
		}
 		
		prescricaoCuidado.setIndItemRecTransferencia(false);
		prescricaoCuidado.setIndItemRecomendadoAlta(false);
		// data de início depende se a prescrição está vigente
		if (this.getPrescricaoMedicaON().isPrescricaoVigente(
				prescricaoCuidado.getPrescricaoMedica())) {
			prescricaoCuidado.setDthrInicio(prescricaoCuidado
					.getPrescricaoMedica().getDthrMovimento());
			// se prescrição vigente
		} else {
			prescricaoCuidado.setDthrInicio(prescricaoCuidado
					.getPrescricaoMedica().getDthrInicio());
			// se prescrição futura
		}
		prescricaoCuidado.setDthrFim(prescricaoCuidado.getPrescricaoMedica()
				.getDthrFim());

		//RN08
		this.validaAtendimentoVigentePermitePrescricao(prescricaoCuidado);
		
		//RN13
		this.validaPrescricaoParaInclusaoCuidado(prescricaoCuidado, nomeMicrocomputador, dataFimVinculoServidor);
		
		this.getMpmPrescricaoCuidadoDAO().persistir(prescricaoCuidado);
		this.getMpmPrescricaoCuidadoDAO().flush();
	}
	
	public void alterarPrescricaoCuidado(MpmPrescricaoCuidado prescricaoCuidadoEdicao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		if (prescricaoCuidadoEdicao == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_PRESCRICAO_CUIDADO_INVALIDO);
		}

		List<DominioIndPendenteItemPrescricao> listIndPendente = new ArrayList<DominioIndPendenteItemPrescricao>();
		listIndPendente.add(DominioIndPendenteItemPrescricao.P);
		listIndPendente.add(DominioIndPendenteItemPrescricao.B);
		listIndPendente.add(DominioIndPendenteItemPrescricao.R);

		// Se Ind_Pendente = P/B/R
		if (listIndPendente.contains(prescricaoCuidadoEdicao.getIndPendente())) {

			// FK cuidadoUsual
			prescricaoCuidadoEdicao.setMpmCuidadoUsuais(this
					.validaAssociacao(prescricaoCuidadoEdicao.getMpmCuidadoUsuais()));

			// FK tipo Aprazamento
			prescricaoCuidadoEdicao.setMpmTipoFreqAprazamentos(this
					.validaAssociacao(prescricaoCuidadoEdicao
							.getMpmTipoFreqAprazamentos()));

			prescricaoCuidadoEdicao.setServidor(this
					.validaAssociacao(prescricaoCuidadoEdicao.getServidor()));

			// RN08
			this.validaAtendimentoVigentePermitePrescricao(prescricaoCuidadoEdicao);
			// RN10
			this.validaAprazamento(
					prescricaoCuidadoEdicao.getMpmTipoFreqAprazamentos(),
					prescricaoCuidadoEdicao.getFrequencia());
			// RN11
			this.validaCuidadoExigeComplemento(prescricaoCuidadoEdicao);
			// RN 14 - Regra exclusiva da Alteração
			this.validaPrescricaoParaAlteracaoCuidado(prescricaoCuidadoEdicao, nomeMicrocomputador, dataFimVinculoServidor);

			this.getMpmPrescricaoCuidadoDAO().merge(prescricaoCuidadoEdicao);
			this.getMpmPrescricaoCuidadoDAO().flush();

			// Ind_Pendente = N
		} else {
			if(DominioIndPendenteItemPrescricao.N.equals(prescricaoCuidadoEdicao.getIndPendente())){
				
				Integer atdSeqOriginal = prescricaoCuidadoEdicao.getPrescricaoMedica().getId().getAtdSeq();
				Long seqOriginal = prescricaoCuidadoEdicao.getId().getSeq();
				
				MpmPrescricaoCuidado prescricaoCuidadoNova = prescricaoCuidadoEdicao;
				
				this.getMpmPrescricaoCuidadoDAO().desatacharCuidado(prescricaoCuidadoEdicao);
				
				
				
				MpmPrescricaoCuidado prescricaoCuidadoOriginal = obterCuidadoPrescritoPorId(new MpmPrescricaoCuidadoId(
						atdSeqOriginal, seqOriginal));
				
				// Criar Novo Registro com Auto Relacionamento
				this.inserePrescricaoCuidadoAutoRelacionamento(prescricaoCuidadoNova,prescricaoCuidadoOriginal, nomeMicrocomputador, dataFimVinculoServidor);
				
				// Atualizar o registro original de Prescricao de Cuidado
				this.atualizarPrescricaoCuidadoOriginal(prescricaoCuidadoOriginal, prescricaoCuidadoNova);
				
				this.getMpmPrescricaoCuidadoDAO().flush();
			}
		}
	}

	private void inserePrescricaoCuidadoAutoRelacionamento(
			MpmPrescricaoCuidado prescricaoCuidadoEdicao,
			MpmPrescricaoCuidado prescricaoCuidadoOriginal,
			String nomeMicrocomputador, 
			final Date dataFimVinculoServidor)
			throws BaseException {
		
		//Trata-se de um insert igual ao criação de um cuidado novo
		//Porém este registro vai ter um auto relacionamento com o cuidado Original
		MpmPrescricaoCuidado prescricaoCuidadoNovo = new MpmPrescricaoCuidado(); 
		
		// FK cuidadoUsual
		prescricaoCuidadoNovo.setMpmCuidadoUsuais(this
				.validaAssociacao(prescricaoCuidadoEdicao.getMpmCuidadoUsuais()));
		
		//Auto Relacionamento
		prescricaoCuidadoNovo.setMpmPrescricaoCuidados(prescricaoCuidadoOriginal);
		
		// FK tipo Aprazamento
		prescricaoCuidadoNovo.setMpmTipoFreqAprazamentos(this
				.validaAssociacao(prescricaoCuidadoEdicao
						.getMpmTipoFreqAprazamentos()));
		
		prescricaoCuidadoNovo.setFrequencia(prescricaoCuidadoEdicao.getFrequencia());
		prescricaoCuidadoNovo.setDescricao(prescricaoCuidadoEdicao.getDescricao());
		prescricaoCuidadoNovo.setId(new MpmPrescricaoCuidadoId(prescricaoCuidadoEdicao
				.getPrescricaoMedica().getId().getAtdSeq(), null));
		prescricaoCuidadoNovo.setServidor(this.validaAssociacao(prescricaoCuidadoEdicao
				.getServidor()));
		prescricaoCuidadoNovo.setCriadoEm(new Date());
		prescricaoCuidadoNovo.setIndPendente(DominioIndPendenteItemPrescricao.P);
		prescricaoCuidadoNovo.setIndItemRecTransferencia(false);
		prescricaoCuidadoNovo.setIndItemRecomendadoAlta(false);
		
		//tratamento problema de lazy
		MpmPrescricaoMedica precricao = mpmPrescricaoMedicaDAO.obterPrescricaoPorId(prescricaoCuidadoEdicao.getPrescricaoMedica().getId());
		
		// data de início depende se a prescrição está vigente
		if (this.getPrescricaoMedicaON().isPrescricaoVigente(
				prescricaoCuidadoEdicao.getPrescricaoMedica())) {
			prescricaoCuidadoNovo.setDthrInicio(precricao.getDthrMovimento());
			// se prescrição vigente
		} else {
			prescricaoCuidadoNovo.setDthrInicio(precricao.getDthrInicio());
			// se prescrição futura
		}
		
		prescricaoCuidadoNovo.setDthrFim(precricao.getDthrFim());
		
		prescricaoCuidadoNovo.setPrescricaoMedica(precricao);
		
		this.validar(prescricaoCuidadoNovo);
		//RN08
		this.validaAtendimentoVigentePermitePrescricao(prescricaoCuidadoNovo);
		//RN13
		this.validaPrescricaoParaInclusaoCuidado(prescricaoCuidadoNovo, nomeMicrocomputador, dataFimVinculoServidor);
		
		this.getMpmPrescricaoCuidadoDAO().persistir(prescricaoCuidadoNovo);
		this.getMpmPrescricaoCuidadoDAO().flush();
	}
	
	private void atualizarPrescricaoCuidadoOriginal(
			MpmPrescricaoCuidado prescricaoCuidadoOriginal, MpmPrescricaoCuidado prescricaoCuidadoEdicao) throws ApplicationBusinessException {
		
		prescricaoCuidadoOriginal.setIndPendente(DominioIndPendenteItemPrescricao.A);
		
		//tratamento problema de lazy
		MpmPrescricaoMedica precricao = mpmPrescricaoMedicaDAO.obterPrescricaoPorId(prescricaoCuidadoEdicao.getPrescricaoMedica().getId());
		
		// data de início depende se a prescrição está vigente
		if (this.getPrescricaoMedicaON().isPrescricaoVigente(
				prescricaoCuidadoEdicao.getPrescricaoMedica())) {
			prescricaoCuidadoOriginal.setDthrFim(precricao.getDthrMovimento());
			// se prescrição vigente
		} else {
			prescricaoCuidadoOriginal.setDthrFim(precricao.getDthrInicio());
			// se prescrição futura
		}
		
		prescricaoCuidadoOriginal.setAlteradoEm(new Date());
		prescricaoCuidadoOriginal.setServidorMovimentado(prescricaoCuidadoEdicao.getServidor());
		
		this.getMpmPrescricaoCuidadoDAO().merge(prescricaoCuidadoOriginal);
	}
	
	public void removerPrescricaoCuidado(MpmPrescricaoCuidado prescricaoCuidado) throws ApplicationBusinessException{
		prescricaoCuidado = this.getMpmPrescricaoCuidadoDAO().merge(prescricaoCuidado);
		if(DominioIndPendenteItemPrescricao.B.equals(prescricaoCuidado.getIndPendente()) ||
		   (DominioIndPendenteItemPrescricao.P.equals(prescricaoCuidado.getIndPendente()) &&
		    prescricaoCuidado.getMpmPrescricaoCuidados() == null )){
			this.getMpmPrescricaoCuidadoDAO().remover(prescricaoCuidado);
			this.getMpmPrescricaoCuidadoDAO().flush();
		
		}else{
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			if(DominioIndPendenteItemPrescricao.N.equals(prescricaoCuidado.getIndPendente())){
				
				prescricaoCuidado.setIndPendente(DominioIndPendenteItemPrescricao.E);
				
				// data de início depende se a prescrição está vigente
				if (this.getPrescricaoMedicaON().isPrescricaoVigente(
						prescricaoCuidado.getPrescricaoMedica())) {
					prescricaoCuidado.setDthrFim(prescricaoCuidado
							.getPrescricaoMedica().getDthrMovimento());
					// se prescrição vigente
				} else {
					prescricaoCuidado.setDthrFim(prescricaoCuidado
							.getPrescricaoMedica().getDthrInicio());
					// se prescrição futura
				}
				
				prescricaoCuidado.setAlteradoEm(new Date());
				prescricaoCuidado.setServidorMovimentado(servidorLogado);
				this.getMpmPrescricaoCuidadoDAO().flush();
			
			}else{
				if(DominioIndPendenteItemPrescricao.P.equals(prescricaoCuidado.getIndPendente()) &&
				   prescricaoCuidado.getMpmPrescricaoCuidados() != null ){
					
					MpmPrescricaoCuidado prescricaoCuidadoAutoRelac = prescricaoCuidado.getMpmPrescricaoCuidados();
					
					prescricaoCuidadoAutoRelac.setIndPendente(DominioIndPendenteItemPrescricao.E);
					prescricaoCuidadoAutoRelac.setServidorMovimentado(servidorLogado);
					
					this.getMpmPrescricaoCuidadoDAO().remover(prescricaoCuidado);
					this.getMpmPrescricaoCuidadoDAO().flush();
					
					//this.getMpmPrescricaoCuidadoDAO().flush();
				}
			}
		}
	}

	private void validaAtendimentoVigentePermitePrescricao(
			MpmPrescricaoCuidado prescricaoCuidado) throws ApplicationBusinessException {
		// verifica se o atendimento existe, é vigente e permite prescrição
		this.getPrescricaoMedicaRN().verificaAtendimento(
				prescricaoCuidado.getDthrInicio(),
				prescricaoCuidado.getDthrFim(),
				prescricaoCuidado.getPrescricaoMedica().getId().getAtdSeq(),
				null, null, null);
	}

	private void validaPrescricaoParaInclusaoCuidado(
			MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		// verifica se a prescrição é valida e atualiza
		// dthr_inicio_mvto_pendente
		// conforme critérios
		this.getPrescricaoMedicaRN().verificaPrescricaoMedica(
				prescricaoCuidado.getPrescricaoMedica().getId().getAtdSeq(),
				prescricaoCuidado.getDthrInicio(),
				prescricaoCuidado.getDthrFim(),
				prescricaoCuidado.getCriadoEm(),
				prescricaoCuidado.getIndPendente(), "I", nomeMicrocomputador, dataFimVinculoServidor);
	}

	private void validaPrescricaoParaAlteracaoCuidado(
			MpmPrescricaoCuidado prescricaoCuidado, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		// E verifica se o término deste item de prescrição é posterior a última
		// prescrição médica.
		// Atualiza prescricão indicando a operação de UPDATE		
		this.getPrescricaoMedicaRN().verificaPrescricaoMedicaUpdate(
				prescricaoCuidado.getPrescricaoMedica().getId().getAtdSeq(),
				prescricaoCuidado.getDthrInicio(),
				prescricaoCuidado.getDthrFim(),
				prescricaoCuidado.getCriadoEm(),
				prescricaoCuidado.getIndPendente(), "U", nomeMicrocomputador, dataFimVinculoServidor);
	}

	private void validar(MpmPrescricaoCuidado prescricaoCuidado)
			throws ApplicationBusinessException {
		
		validaCuidadoExigeComplemento(prescricaoCuidado);

		// valida outras informações do aprazamento
		validaAprazamento(prescricaoCuidado.getMpmTipoFreqAprazamentos(),
				prescricaoCuidado.getFrequencia());
	}

	/**
	 * Verifica se cuidado usual foi informado, existe e está ativo
	 * 
	 * @param modeloBasicoCuidado
	 * @return modeloBasicoCuidado
	 * @throws ApplicationBusinessException
	 */
	private MpmCuidadoUsual validaAssociacao(MpmCuidadoUsual cuidadoUsual)
			throws ApplicationBusinessException {

		MpmCuidadoUsual result = null;

		if (cuidadoUsual == null || cuidadoUsual.getSeq() == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_CUIDADO_USUAL_INVALIDO);
		}

		result = this.getMpmCuidadoUsualDAO().obterPorChavePrimaria(
				cuidadoUsual.getSeq());

		if (result == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_CUIDADO_USUAL_INVALIDO);
		}

		return result;
	}

	/**
	 * Valida se o Tipo de Frequencia foi informado, existe e está ativo
	 * 
	 * @param tipoFrequenciaAprazamento
	 * @return tipoFrequenciaAprazamento
	 * @throws ApplicationBusinessException
	 */
	protected MpmTipoFrequenciaAprazamento validaAssociacao(
			MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento)
			throws ApplicationBusinessException {

		MpmTipoFrequenciaAprazamento result = null;

		if (mpmTipoFrequenciaAprazamento != null) {
			result = this.getMpmTipoFrequenciaAprazamentoDAO()
					.obterPorChavePrimaria(
							mpmTipoFrequenciaAprazamento.getSeq());
		}

		if (result == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_TIPO_FREQUENCIA_INVALIDO);
		}

		if (!result.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_TIPO_FREQUENCIA_INATIVO);
		}

		return result;
	}

	/**
	 * Verifica se o servidor associado é válido
	 * 
	 * @param servidor
	 * @return servidor carregado
	 * @throws ApplicationBusinessException
	 */
	protected RapServidores validaAssociacao(RapServidores servidor)
			throws ApplicationBusinessException {
		// não informado
		if (servidor == null || servidor.getId() == null
				| servidor.getId().getVinCodigo() == null
				|| servidor.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}

		RapServidores result = this.getRegistroColaboradorFacade().buscaServidor(
				servidor.getId());
		if (result == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}

		return result;
	}
	
	private void validaCuidadoExigeComplemento(
			MpmPrescricaoCuidado prescricaoCuidado)
			throws ApplicationBusinessException {

		// se exige complemento
		if (prescricaoCuidado.getMpmCuidadoUsuais().getIndDigitaComplemento()
				&& StringUtils.isBlank(prescricaoCuidado.getDescricao())) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_CUIDADO_USUAL_EXIGE_COMPLEMENTO);
		}
	}

	/**
	 * Valida tipo de frequência
	 * 
	 * @param itemDieta
	 * @throws ApplicationBusinessException
	 */
	private void validaAprazamento(MpmTipoFrequenciaAprazamento tipo,
			Integer frequencia) throws ApplicationBusinessException {

		if (frequencia != null && tipo == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoCuidadoExceptionCode.MENSAGEM_INFORME_FREQUENCIA_E_TIPO);
		}

		if (tipo != null) {
			if (tipo.getIndDigitaFrequencia() && (frequencia == null
					||frequencia.equals(0))) {
				throw new ApplicationBusinessException(
						ManterPrescricaoCuidadoExceptionCode.MENSAGEM_TIPO_FREQUENCIA_EXIGE_INFORMACAO_FREQUENCIA);
			}

			if (!tipo.getIndDigitaFrequencia() && frequencia != null) {
				throw new ApplicationBusinessException(
						ManterPrescricaoCuidadoExceptionCode.MENSAGEM_TIPO_FREQUENCIA_NAO_PERMITE_INFORMACAO);
			}
		}
	}

	public MpmPrescricaoCuidado obterCuidadoPrescritoPorId(
			MpmPrescricaoCuidadoId id) {
		return this.getMpmPrescricaoCuidadoDAO().obterPorChavePrimaria(id, MpmPrescricaoCuidado.Fields.MPM_TIPO_FREQ_APRAZAMENTO, MpmPrescricaoCuidado.Fields.CDU);
	}

	public List<MpmPrescricaoCuidado> obterListaCuidadosPrescritos(
			MpmPrescricaoMedicaId prescricaoMedicaId, Date dthrFim) {
		return this.getMpmPrescricaoCuidadoDAO().pesquisarCuidadosMedicos(
				prescricaoMedicaId, dthrFim, false);
	}

	protected MpmCuidadoUsualDAO getMpmCuidadoUsualDAO() {
		return mpmCuidadoUsualDAO;
	}

	protected MpmPrescricaoCuidadoDAO getMpmPrescricaoCuidadoDAO() {
		return mpmPrescricaoCuidadoDAO;
	}

	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	protected PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}

	protected PrescricaoMedicaON getPrescricaoMedicaON() {
		return prescricaoMedicaON;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
