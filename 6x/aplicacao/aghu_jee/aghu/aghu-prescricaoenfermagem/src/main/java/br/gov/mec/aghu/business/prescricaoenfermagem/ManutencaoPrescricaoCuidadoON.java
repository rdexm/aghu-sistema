package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioIndPendentePrescricoesCuidados;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.model.EpePrescCuidDiagnostico;
import br.gov.mec.aghu.model.EpePrescCuidDiagnosticoId;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.EpePrescricoesCuidadosId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.CuidadoVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author dansantos / diego.pacheco
 *
 */
@Stateless
public class ManutencaoPrescricaoCuidadoON extends BaseBusiness {

	@EJB
	private ManutencaoPrescricaoCuidadoRN manutencaoPrescricaoCuidadoRN;
	
	private static final Log LOG = LogFactory.getLog(ManutencaoPrescricaoCuidadoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	private static final long serialVersionUID = -4297398747360851368L;

	public enum ManutencaoPrescricaoCuidadoONExceptionCode implements
		BusinessExceptionCode {
		MENSAGEM_PRESCRICAO_CUIDADO_INVALIDO, MENSAGEM_INFORME_FREQUENCIA_E_TIPO, 
		MENSAGEM_TIPO_FREQUENCIA_EXIGE_INFORMACAO_FREQUENCIA, MPM_00752, 
		ERRO_PRESCRICAO_CUIDADO_ATENDIMENTO_PRESCRICAO_NULO,ERRO_PRESCRICAO_CUIDADO_TIPO_APRAZAMENTO_NULO,
		MENSAGEM_SERVIDOR_INVALIDO, MENSAGEM_TIPO_FREQUENCIA_INVALIDO, MENSAGEM_TIPO_FREQUENCIA_INATIVO;
	}	
	
	/**
	 * Verifica se existe algum item na lista de itens da prescrição que devem
	 * ser excluido, caso exista chama o metodo de exclusão e remove o objeto da
	 * lista.
	 * 
	 * @param prescricaoEnfermagem
	 * @param {List<CuidadoVO>} listaCuidadoVO
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public void removerCuidadosSelecionados(List<CuidadoVO> listaCuidadoVO) throws ApplicationBusinessException { 
			
		if (listaCuidadoVO != null) {
			for (int i = listaCuidadoVO.size() - 1; i >= 0; i--) {
				CuidadoVO cuidadoVO = listaCuidadoVO.get(i);

				if (cuidadoVO.getExcluir() != null && cuidadoVO.getExcluir()) {
					EpePrescricoesCuidados prescricaoCuidado = cuidadoVO.getPrescricaoCuidado();
					// Remove do banco quando existe a prescricao do cuidado
					if (prescricaoCuidado != null && prescricaoCuidado.getId()!=null && prescricaoCuidado.getId().getSeq()!=null) {
						this.removerPrescricaoCuidado(prescricaoCuidado);
					}
					// Mesmo que não exista no banco, deve remover da lista sempre
					// quando é chamado o removerCuidadosSelecionados.
					listaCuidadoVO.remove(cuidadoVO);
				}
			}
		}
	}
	
	public void removerPrescricaoCuidado(
			EpePrescricoesCuidados prescricaoCuidado) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		prescricaoCuidado = this.getPrescricaoCuidadoDAO().carregarPorChavePrimaria(prescricaoCuidado.getId());
		
		if (DominioIndPendentePrescricoesCuidados.P.equals(prescricaoCuidado
				.getPendente())) {
			List<EpePrescCuidDiagnostico> lista = this.getEpePrescCuidDiagnosticoDAO().listarPrescCuidDiagnosticoPorPrescricaoCuidado(prescricaoCuidado);
			if(lista!=null){
				for(EpePrescCuidDiagnostico prescCuidDiagnostico: lista){
					this.getEpePrescCuidDiagnosticoDAO().remover(prescCuidDiagnostico);
					this.getEpePrescCuidDiagnosticoDAO().flush();
				}
			}
			
			//Atualiza dados da prescricao de cuidado anterior.
			if (prescricaoCuidado.getPrescricaoCuidado()!=null){
				EpePrescricoesCuidados prescricaoCuidadoPai = prescricaoCuidado.getPrescricaoCuidado();
				
				prescricaoCuidadoPai.setPendente(DominioIndPendentePrescricoesCuidados.E);
				//prescricaoCuidadoPai.setAlteradoEm(new Date());
				prescricaoCuidadoPai.setServidorMovimentado(servidorLogado);
				
				this.getPrescricaoCuidadoDAO().merge(prescricaoCuidadoPai);
			}
			
			this.getPrescricaoCuidadoDAO().remover(prescricaoCuidado);
			this.getPrescricaoCuidadoDAO().flush();
		} else if (DominioIndPendentePrescricoesCuidados.N.equals(prescricaoCuidado.getPendente())) {

			prescricaoCuidado.setPendente(DominioIndPendentePrescricoesCuidados.E);

			// data de início depende se a prescrição está vigente
			if (this.isPrescricaoVigente(prescricaoCuidado.getPrescricaoEnfermagem())) {
				prescricaoCuidado.setDthrFim(prescricaoCuidado.getPrescricaoEnfermagem().getDthrMovimento());
				// se prescrição vigente
			} else {
				prescricaoCuidado.setDthrFim(prescricaoCuidado.getPrescricaoEnfermagem().getDthrInicio());
				// se prescrição futura
			}

			prescricaoCuidado.setAlteradoEm(new Date());
			prescricaoCuidado.setServidorMovimentado(servidorLogado);

			this.getPrescricaoCuidadoDAO().merge(prescricaoCuidado);
			this.getPrescricaoCuidadoDAO().flush();

		} 
	}

	private boolean isPrescricaoVigente(
			EpePrescricaoEnfermagem prescricaoEnfermagem) {
		if (prescricaoEnfermagem == null) {
			throw new IllegalArgumentException("Argumento inválido");
		}
		long hoje = Calendar.getInstance().getTimeInMillis();

		long inicio = prescricaoEnfermagem.getDthrInicio().getTime();
		long fim = prescricaoEnfermagem.getDthrFim().getTime();
		if (hoje >= inicio && hoje < fim) {
			return true;
		}
		return false;
	}
	
	public EpePrescricoesCuidados inserirPrescricaoCuidado(EpePrescricoesCuidados prescricaoCuidado, String descricao)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (prescricaoCuidado == null) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.MENSAGEM_PRESCRICAO_CUIDADO_INVALIDO);
		}
		
		if (prescricaoCuidado.getTipoFrequenciaAprazamento()==null){
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.ERRO_PRESCRICAO_CUIDADO_TIPO_APRAZAMENTO_NULO, descricao);
		}
		
		Integer atdSeq = null; 
		if (prescricaoCuidado.getPrescricaoEnfermagem()==null || prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento()== null){
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.ERRO_PRESCRICAO_CUIDADO_ATENDIMENTO_PRESCRICAO_NULO);
		}else{
			atdSeq = prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento().getSeq();
		}
	
		this.getManutencaoPrescricaoCuidadoRN().verificarDescricaoCuidado(prescricaoCuidado);
		
		this.validarAprazamento(prescricaoCuidado.getTipoFrequenciaAprazamento(), prescricaoCuidado.getFrequencia());
		
		// data de início depende se a prescrição está vigente
		if (this.isPrescricaoVigente(
				prescricaoCuidado.getPrescricaoEnfermagem())) {
			prescricaoCuidado.setDthrInicio(prescricaoCuidado
					.getPrescricaoEnfermagem().getDthrMovimento());
			// se prescrição vigente
		} else {
			prescricaoCuidado.setDthrInicio(prescricaoCuidado
					.getPrescricaoEnfermagem().getDthrInicio());
			// se prescrição futura
		}
		prescricaoCuidado.setDthrFim(prescricaoCuidado.getPrescricaoEnfermagem()
				.getDthrFim());
		
		prescricaoCuidado.setServidor(servidorLogado);
		prescricaoCuidado.setCriadoEm(new Date());
		
		//RN08
		this.validarAtendimentoVigentePermitePrescricao(prescricaoCuidado);
		
		//RN13
		this.validarPrescricaoParaInclusaoCuidado(prescricaoCuidado);
		
		Integer seqPrescricaoCuidado = getPrescricaoCuidadoDAO().obterValorSequencialId();
		EpePrescricoesCuidadosId idPrescricaoCuidado = new EpePrescricoesCuidadosId(atdSeq,seqPrescricaoCuidado);
		
		prescricaoCuidado.setId(idPrescricaoCuidado);
		this.getPrescricaoCuidadoDAO().persistir(prescricaoCuidado);
		this.getPrescricaoCuidadoDAO().flush();
		
		return prescricaoCuidado;
	}
	
	public void inserirPrescCuidDiagnostico(EpePrescricoesCuidados prescricaoCuidado, CuidadoVO cuidadoVO){
			
		EpePrescCuidDiagnosticoId id = new EpePrescCuidDiagnosticoId();
			
		if (prescricaoCuidado.getId()!=null){
			id.setPrcAtdSeq(prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento().getSeq());
			id.setPrcSeq(prescricaoCuidado.getId().getSeq());
		}
		id.setCdgCuiSeq(prescricaoCuidado.getCuidado().getSeq());
		id.setCdgFdgDgnSequencia(cuidadoVO.getFdgDgnSequencia());
		id.setCdgFdgDgnSnbGnbSeq(cuidadoVO.getFdgDgnSnbGnbSeq());
		id.setCdgFdgDgnSnbSequencia(cuidadoVO.getFdgDgnSnbSequencia());
		id.setCdgFdgFreSeq(cuidadoVO.getFdgFreSeq());
		
		EpePrescCuidDiagnostico precCuidDiagnostico = new EpePrescCuidDiagnostico(id);
		getEpePrescCuidDiagnosticoDAO().persistir(precCuidDiagnostico);
		getEpePrescCuidDiagnosticoDAO().flush();
	}
	
	
	public void validarAprazamento(MpmTipoFrequenciaAprazamento tipo,
			Short frequencia) throws ApplicationBusinessException {

		if (frequencia != null && tipo == null) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.MENSAGEM_INFORME_FREQUENCIA_E_TIPO);
		}

		if (tipo != null) {
			if (tipo.getIndDigitaFrequencia() && (frequencia == null
					||frequencia.equals(Short.valueOf("0")))) {
				throw new ApplicationBusinessException(
						ManutencaoPrescricaoCuidadoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_EXIGE_INFORMACAO_FREQUENCIA);
			}

			if (!tipo.getIndDigitaFrequencia() && frequencia != null) {
				throw new ApplicationBusinessException(
						ManutencaoPrescricaoCuidadoONExceptionCode.MPM_00752);
			}
		}
	}
	
	private void validarAtendimentoVigentePermitePrescricao(
			EpePrescricoesCuidados prescricaoCuidado) throws ApplicationBusinessException {
		// verifica se o atendimento existe, é vigente e permite prescrição
		this.getPrescricaoMedicaFacade().verificarAtendimento(
						prescricaoCuidado.getDthrInicio(),
						prescricaoCuidado.getDthrFim(),
						prescricaoCuidado.getPrescricaoEnfermagem().getId()
								.getAtdSeq(), null, null, null);
	}
	
	private void validarPrescricaoParaInclusaoCuidado(
			EpePrescricoesCuidados prescricaoCuidado) throws ApplicationBusinessException {
		Integer atdSeq = null;
		if(prescricaoCuidado.getPrescricaoEnfermagem()!=null && prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento()!=null){
			atdSeq = prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento().getSeq();
		}
		this.getManutencaoPrescricaoCuidadoRN()
				.verificarPrescricaoEnfermagemInsert(atdSeq,
						prescricaoCuidado.getDthrInicio(),
						prescricaoCuidado.getDthrFim(),
						prescricaoCuidado.getCriadoEm(),
						prescricaoCuidado.getPendente(),
						DominioOperacaoBanco.INS);
	}
	
	public EpePrescricoesCuidados alterarPrescricaoCuidado(EpePrescricoesCuidados prescricaoCuidadoEdicao, String descricao, Boolean cuidadosAnterioresComErro) throws BaseException {

		EpePrescricoesCuidados prescricaoCuidadoRetorno = prescricaoCuidadoEdicao;
		
		if (prescricaoCuidadoEdicao == null) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.MENSAGEM_PRESCRICAO_CUIDADO_INVALIDO);
		}
		
		if (prescricaoCuidadoEdicao.getTipoFrequenciaAprazamento()==null){
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.ERRO_PRESCRICAO_CUIDADO_TIPO_APRAZAMENTO_NULO, descricao);
		}
		
		List<DominioIndPendenteItemPrescricao> listIndPendente = new ArrayList<DominioIndPendenteItemPrescricao>();
		listIndPendente.add(DominioIndPendenteItemPrescricao.P);
		listIndPendente.add(DominioIndPendenteItemPrescricao.B);
		listIndPendente.add(DominioIndPendenteItemPrescricao.R);
		
		// Se Ind_Pendente = P/B/R
		if (listIndPendente.toString().contains(prescricaoCuidadoEdicao.getPendente().toString())) {
		
			// FK tipo Aprazamento
			prescricaoCuidadoEdicao.setTipoFrequenciaAprazamento(this
					.validarTipoFreqAprazamento(prescricaoCuidadoEdicao.getTipoFrequenciaAprazamento()));
		
			prescricaoCuidadoEdicao.setServidor(this.validarServidor(prescricaoCuidadoEdicao.getServidor()));
		
			// RN08
			this.validarAtendimentoVigentePermitePrescricao(prescricaoCuidadoEdicao);
			// RN10
			this.validarAprazamento(prescricaoCuidadoEdicao.getTipoFrequenciaAprazamento(),
					prescricaoCuidadoEdicao.getFrequencia());
			// RN11
			this.getManutencaoPrescricaoCuidadoRN().verificarDescricaoCuidado(prescricaoCuidadoEdicao);
			// RN 14 - Regra exclusiva da Alteração
			this.validaPrescricaoParaAlteracaoCuidado(prescricaoCuidadoEdicao);
			if(!cuidadosAnterioresComErro){
				this.getPrescricaoCuidadoDAO().merge(prescricaoCuidadoEdicao);
				this.getPrescricaoCuidadoDAO().flush();
			}

		// Ind_Pendente = N
		} else if(DominioIndPendenteItemPrescricao.N.toString().equals(prescricaoCuidadoEdicao.getPendente().toString())){
			
			Integer atdSeqOriginal = prescricaoCuidadoEdicao.getPrescricaoEnfermagem().getId().getAtdSeq();
			Integer seqOriginal = prescricaoCuidadoEdicao.getId().getSeq();
			
			EpePrescricoesCuidados prescricaoCuidadoNova = prescricaoCuidadoEdicao;
			
			this.getPrescricaoCuidadoDAO().desatacharCuidado(prescricaoCuidadoEdicao);
			
			EpePrescricoesCuidados prescricaoCuidadoOriginal = getPrescricaoCuidadoDAO()
				.obterPorChavePrimaria(new EpePrescricoesCuidadosId(atdSeqOriginal, seqOriginal));
			
			// verificar se realmente alterou
			if (this.verificarAlteracaoCuidado(prescricaoCuidadoOriginal, prescricaoCuidadoNova)){

				// Cria Novo Registro com Auto Relacionamento
				prescricaoCuidadoRetorno = this.inserirPrescricaoCuidadoAutoRelacionamento(prescricaoCuidadoNova, prescricaoCuidadoOriginal);
				
				// Atualiza o registro original de Prescricao de Cuidado
				if(!cuidadosAnterioresComErro){	
					this.atualizarPrescricaoCuidadoOriginal(prescricaoCuidadoOriginal, prescricaoCuidadoNova);					
					this.getPrescricaoCuidadoDAO().flush();
				}
			}
		}
		return prescricaoCuidadoRetorno;
	}
	
	public boolean verificarAlteracaoCuidado(EpePrescricoesCuidados prescricaoCuidadoOriginal, EpePrescricoesCuidados prescricaoCuidadoNova){
		
		if ((prescricaoCuidadoOriginal.getFrequencia()!=null && prescricaoCuidadoNova.getFrequencia()==null) ||
				(prescricaoCuidadoNova.getFrequencia()!=null && prescricaoCuidadoOriginal.getFrequencia()==null) ||
				(prescricaoCuidadoOriginal.getFrequencia()!=null && prescricaoCuidadoNova.getFrequencia()!=null &&
						!(prescricaoCuidadoOriginal.getFrequencia().shortValue()==prescricaoCuidadoNova.getFrequencia().shortValue()))){
			return true;
		}
		
		if (!prescricaoCuidadoOriginal.getTipoFrequenciaAprazamento().getDescricao().equalsIgnoreCase(
										prescricaoCuidadoNova.getTipoFrequenciaAprazamento().getDescricao())
										|| ((prescricaoCuidadoOriginal.getDescricao() !=null && !prescricaoCuidadoOriginal.getDescricao().equals(prescricaoCuidadoNova.getDescricao())
												||(prescricaoCuidadoOriginal.getDescricao() == null && prescricaoCuidadoNova.getDescricao() != null)))){
			return true;
		}
		
		
		return false;
	}
	
	
	/**
	 * Verifica se o servidor associado é válido
	 * 
	 * @param servidor
	 * @return servidor carregado
	 * @throws ApplicationBusinessException
	 */
	protected RapServidores validarServidor(RapServidores servidor) throws ApplicationBusinessException {
		// não informado
		if (servidor == null || servidor.getId() == null
				| servidor.getId().getVinCodigo() == null
				|| servidor.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
		}

		RapServidores result = this.getRegistroColaboradorFacade().buscaServidor(
				servidor.getId());
		if (result == null) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.MENSAGEM_SERVIDOR_INVALIDO);
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
	protected MpmTipoFrequenciaAprazamento validarTipoFreqAprazamento(
			MpmTipoFrequenciaAprazamento mpmTipoFrequenciaAprazamento)
			throws ApplicationBusinessException {

		MpmTipoFrequenciaAprazamento result = null;

		if (mpmTipoFrequenciaAprazamento != null) {
			result = getPrescricaoMedicaFacade()
					.obterTipoFrequenciaAprazamentoId(mpmTipoFrequenciaAprazamento.getSeq());
		}

		if (result == null) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_INVALIDO);
		}

		if (!result.getIndSituacao().isAtivo()) {
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.MENSAGEM_TIPO_FREQUENCIA_INATIVO);
		}

		return result;
	}
	
	private void validaPrescricaoParaAlteracaoCuidado(
			EpePrescricoesCuidados prescricaoCuidado) throws ApplicationBusinessException {
		// E verifica se o término deste item de prescrição é posterior a última
		// prescrição médica.
		// Atualiza prescricão indicando a operação de UPDATE
		this.getManutencaoPrescricaoCuidadoRN().verificarPrescricaoEnfermagemUpdate(
				prescricaoCuidado.getPrescricaoEnfermagem().getId().getAtdSeq(),
				prescricaoCuidado.getDthrInicio(),
				prescricaoCuidado.getDthrFim(),
				prescricaoCuidado.getCriadoEm(),
				prescricaoCuidado.getPendente(), DominioOperacaoBanco.UPD);
	}
	
	private EpePrescricoesCuidados inserirPrescricaoCuidadoAutoRelacionamento(
			EpePrescricoesCuidados prescricaoCuidadoEdicao,
			EpePrescricoesCuidados prescricaoCuidadoOriginal)
			throws BaseException {
		
		//Trata-se de um insert igual ao criação de um cuidado novo
		//Porém este registro vai ter um auto relacionamento com o cuidado Original
		EpePrescricoesCuidados prescricaoCuidadoNovo = new EpePrescricoesCuidados();
		
		Integer atdSeq = null; 
		if (prescricaoCuidadoEdicao.getPrescricaoEnfermagem()==null || prescricaoCuidadoEdicao.getPrescricaoEnfermagem().getAtendimento()== null){
			throw new ApplicationBusinessException(
					ManutencaoPrescricaoCuidadoONExceptionCode.ERRO_PRESCRICAO_CUIDADO_ATENDIMENTO_PRESCRICAO_NULO);
		}else{
			atdSeq = prescricaoCuidadoEdicao.getPrescricaoEnfermagem().getAtendimento().getSeq();
		}
		Integer seqPrescricaoCuidado = getPrescricaoCuidadoDAO().obterValorSequencialId();
		EpePrescricoesCuidadosId idPrescricaoCuidado = new EpePrescricoesCuidadosId(atdSeq,seqPrescricaoCuidado);
		
		prescricaoCuidadoNovo.setId(idPrescricaoCuidado);
		
		//Auto Relacionamento
		prescricaoCuidadoNovo.setPrescricaoCuidado(prescricaoCuidadoOriginal);
		
		// FK tipo Aprazamento
		prescricaoCuidadoNovo.setTipoFrequenciaAprazamento(this
				.validarTipoFreqAprazamento(prescricaoCuidadoEdicao
						.getTipoFrequenciaAprazamento()));
		
		prescricaoCuidadoNovo.setFrequencia(prescricaoCuidadoEdicao.getFrequencia());
		prescricaoCuidadoNovo.setDescricao(prescricaoCuidadoEdicao.getDescricao());
		prescricaoCuidadoNovo.setServidor(this.validarServidor(prescricaoCuidadoEdicao.getServidor()));
		prescricaoCuidadoNovo.setCriadoEm(new Date());
		prescricaoCuidadoNovo.setPendente(DominioIndPendentePrescricoesCuidados.P);
		prescricaoCuidadoNovo.setCuidado(prescricaoCuidadoOriginal.getCuidado());
		
		// data de início depende se a prescrição está vigente
		if (this.isPrescricaoVigente(prescricaoCuidadoEdicao.getPrescricaoEnfermagem())) {
			prescricaoCuidadoNovo.setDthrInicio(prescricaoCuidadoEdicao.getPrescricaoEnfermagem().getDthrMovimento());
			// se prescrição vigente
		} else {
			prescricaoCuidadoNovo.setDthrInicio(prescricaoCuidadoEdicao	.getPrescricaoEnfermagem().getDthrInicio());
			// se prescrição futura
		}
		
		prescricaoCuidadoNovo.setDthrFim(prescricaoCuidadoEdicao.getPrescricaoEnfermagem().getDthrFim());
		
		prescricaoCuidadoNovo.setPrescricaoEnfermagem(prescricaoCuidadoEdicao.getPrescricaoEnfermagem());
		
		this.validarComplementoEAprazamento(prescricaoCuidadoNovo);
		//RN08
		this.validarAtendimentoVigentePermitePrescricao(prescricaoCuidadoNovo);
		//RN13
		this.validarPrescricaoParaInclusaoCuidado(prescricaoCuidadoNovo);
		
		this.getPrescricaoCuidadoDAO().persistir(prescricaoCuidadoNovo);
		
		return prescricaoCuidadoNovo;
	}
	
	private void validarComplementoEAprazamento(EpePrescricoesCuidados prescricaoCuidado)
		throws ApplicationBusinessException {
	
		this.getManutencaoPrescricaoCuidadoRN().verificarDescricaoCuidado(prescricaoCuidado);
		
		// valida outras informações do aprazamento
		validarAprazamento(prescricaoCuidado.getTipoFrequenciaAprazamento(), prescricaoCuidado.getFrequencia());
	}
	
	private void atualizarPrescricaoCuidadoOriginal(EpePrescricoesCuidados prescricaoCuidadoOriginal, 
			EpePrescricoesCuidados prescricaoCuidadoEdicao) throws ApplicationBusinessException {
		
		prescricaoCuidadoOriginal.setPendente(DominioIndPendentePrescricoesCuidados.A);
		
		// data de início depende se a prescrição está vigente
		if (this.isPrescricaoVigente(prescricaoCuidadoEdicao.getPrescricaoEnfermagem())) {
			prescricaoCuidadoOriginal.setDthrFim(prescricaoCuidadoEdicao.getPrescricaoEnfermagem().getDthrMovimento());
			// se prescrição vigente
		} else {
			prescricaoCuidadoOriginal.setDthrFim(prescricaoCuidadoEdicao.getPrescricaoEnfermagem().getDthrInicio());
			// se prescrição futura
		}
		
		prescricaoCuidadoOriginal.setAlteradoEm(new Date());
		prescricaoCuidadoOriginal.setServidorMovimentado(prescricaoCuidadoEdicao.getServidor());
		
		this.getPrescricaoCuidadoDAO().merge(prescricaoCuidadoOriginal);
		this.getPrescricaoCuidadoDAO().atualizar(prescricaoCuidadoOriginal);
	}	
	
	/*
	public void persistirPrescricaoCuidado(CuidadoVO cuidadoVO) throws BaseException { 
		EpePrescricoesCuidados prescricaoCuidado = cuidadoVO.getPrescricaoCuidado();
		if (prescricaoCuidado != null && prescricaoCuidado.getPrescricaoEnfermagem() != null) {
			alterarPrescricaoCuidado(prescricaoCuidado);
		}
		else {
			prescricaoCuidado = cuidadoVO.getPrescricaoCuidado();
			prescricaoCuidado.setCuidado(cuidadoVO.getCuidado());
			inserirPrescricaoCuidado(prescricaoCuidado);
		}
	}*/
	
	public List<CuidadoVO> pesquisarCuidadosPrescEnfermagem(Integer penSeq, Integer penAtdSeq, Date dthrFim){
		
		List<CuidadoVO> listaCuidadoVO  = new ArrayList<CuidadoVO>();
		List<EpePrescricoesCuidados> listaCuidados = getPrescricaoCuidadoDAO().pesquisarCuidadosAtivosAtribuidos(penSeq, penAtdSeq, dthrFim);
	
		for (EpePrescricoesCuidados prescricaoCuidado : listaCuidados) {
			
			CuidadoVO cuidadoVO = new CuidadoVO();
			cuidadoVO.setPrescricaoEnfermagem(prescricaoCuidado.getPrescricaoEnfermagem());
			cuidadoVO.setPrescricaoCuidado(prescricaoCuidado);
			cuidadoVO.setCuidado(prescricaoCuidado.getCuidado());
			cuidadoVO.setDescricao(prescricaoCuidado.getCuidado().getDescricao());
			
			if (prescricaoCuidado.getFrequencia()==null){
				cuidadoVO.setListaTipoFrequenciaAprazamento(prescricaoMedicaFacade
						.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(true, null));
				cuidadoVO.getPrescricaoCuidado().getTipoFrequenciaAprazamento().
				setDescricaoFormatada(cuidadoVO.getPrescricaoCuidado().getTipoFrequenciaAprazamento().getDescricao());						
			
			}else{
				cuidadoVO.setListaTipoFrequenciaAprazamento(prescricaoMedicaFacade
						.obterListaTipoFrequenciaAprazamentoDigitaFrequencia(false, null));
				// Adicionar a descrição com a frequencia
				if (prescricaoCuidado.getTipoFrequenciaAprazamento().getSintaxe()!=null){
					cuidadoVO.getPrescricaoCuidado().getTipoFrequenciaAprazamento().
					getDescricaoSintaxeFormatada(cuidadoVO.getPrescricaoCuidado().getFrequencia());
				}						
			}
			
			listaCuidadoVO.add(cuidadoVO);
		}
	
		return listaCuidadoVO;
	}
	
	protected EpePrescricoesCuidadosDAO getPrescricaoCuidadoDAO() {
		return epePrescricoesCuidadosDAO;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected ManutencaoPrescricaoCuidadoRN getManutencaoPrescricaoCuidadoRN() {
		return manutencaoPrescricaoCuidadoRN;
	}
	
	protected EpePrescCuidDiagnosticoDAO getEpePrescCuidDiagnosticoDAO(){
		return epePrescCuidDiagnosticoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
