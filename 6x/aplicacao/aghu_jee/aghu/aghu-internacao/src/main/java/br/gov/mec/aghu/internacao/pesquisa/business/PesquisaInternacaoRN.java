package br.gov.mec.aghu.internacao.pesquisa.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinDiariasAutorizadasDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PesquisaInternacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaInternacaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@Inject
	private AinQuartosDAO ainQuartosDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AinDiariasAutorizadasDAO ainDiariasAutorizadasDAO;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2993872743458548080L;

	/**
	 * Enumeracao com os codigos de mensagens de exceções negociais.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 */
	private enum PesquisaInternacaoRNExceptionCode implements BusinessExceptionCode {
		ERRO_DATA_INICIAL_MENOR_QUE_FINAL, ERRO_PERIODO_MAX_RELATORIOS, ERRO_ESPECIALIDADE_INVALIDA, ERRO_CLINICA_INVALIDA, ERRO_CONVENIO_PLANO_INVALIDO, LABEL_INFORMAR_PRONTUARIO_VALIDO
	}

	/**
	 * ORADB Function RAPC_BUSC_NOME_USUAL
	 */
	public String buscarNomeUsual(Short pVinCodigo, Integer pMatricula) {
		String nome = null;
		if (pVinCodigo != null && pMatricula != null) {
			RapServidoresId id = new RapServidoresId(pMatricula, pVinCodigo);
			RapServidores rapServidores = this.getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(id);
			RapPessoasFisicas rapPessoasFisicas = rapServidores.getPessoaFisica();
			nome = rapPessoasFisicas.getNomeUsual() != null ? rapPessoasFisicas.getNomeUsual() : rapPessoasFisicas.getNome();
		}
		return nome;
	}

	/**
	 * ORADB ainc_busca_senha_int
	 */
	public String buscaSenhaInternacao(Integer internacaoSeq) {
		return getAinDiariasAutorizadasDAO().buscaSenhaInternacao(internacaoSeq);
	}

	/**
	 * ORADB ainc_busca_ult_alta
	 */
	public Date buscaUltimaAlta(Integer internacaoSeq) {
		return getAinInternacaoDAO().buscaUltimaAlta(internacaoSeq);
	}

	/**
	 * ORADB Function RAPC_BUSC_NRO_R_CONS
	 */
	public String buscarNroRegistroConselho(Short vinCodigo, Integer matricula) {
		return getRegistroColaboradorFacade().buscarNroRegistroConselho(vinCodigo, matricula);
	}

	/**
	 * Verifica se uma unidade funcional tem determinada característica, se
	 * tiver retorna 'S' senão retorna 'N'.
	 * 
	 * ORADB Function AGHC_VER_CARACT_UNF.
	 * 
	 * @return Valor 'S' ou 'N' indicando se a característica pesquisada foi
	 *         encontrada.
	 */
	public DominioSimNao verificarCaracteristicaDaUnidadeFuncional(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		DominioSimNao result = null;
		AghCaractUnidFuncionaisId id = null;
		AghCaractUnidFuncionais aghCUF = null;

		if (unfSeq != null && caracteristica != null) {
			id = new AghCaractUnidFuncionaisId(unfSeq, caracteristica);
			aghCUF = getAghuFacade().obterAghCaractUnidFuncionaisPorChavePrimaria(id);
		}

		result = (aghCUF == null) ? DominioSimNao.N : DominioSimNao.S;

		return result;
	}

	/**
	 * Localiza a unidade funcional associada a um quarto ou leito a partir dos
	 * parâmetros informados.
	 * 
	 * ORADB Function AINC_RET_UNF_SEQ.
	 * 
	 * @return Id da unidade funcional encontrada.
	 */
	public Short aincRetUnfSeq(Short pUnfSeq, Short pQuarto, String pLeito) {
		Short unfSeq = null;

		if (pUnfSeq != null) {
			// Copia o valor, sem referenciar o mesmo objeto pUnfSeq.
			unfSeq = pUnfSeq.shortValue();
		} else if (pQuarto != null) {
			AinQuartos ainQuartos = null;
			ainQuartos = this.getAinQuartosDAO().obterPorChavePrimaria(pQuarto);

			if (ainQuartos != null && ainQuartos.getUnidadeFuncional() != null) {
				unfSeq = ainQuartos.getUnidadeFuncional().getSeq();
			}
		} else if (pLeito != null) {
			AinLeitos ainLeitos = null;
			ainLeitos = this.getAinLeitosDAO().obterPorChavePrimaria(pLeito);

			if (ainLeitos != null && ainLeitos.getUnidadeFuncional() != null) {
				unfSeq = ainLeitos.getUnidadeFuncional().getSeq();
			}
		}
		return unfSeq;
	}
	
	/**
	 * Localiza a unidade funcional associada a um quarto ou leito a partir dos
	 * parâmetros informados. Sobrecarga do método para quando já se possui os
	 * objetos.
	 * 
	 * ORADB Function AINC_RET_UNF_SEQ.
	 * 
	 * @return Id da unidade funcional encontrada.
	 */
	public Short aincRetUnfSeq(AghUnidadesFuncionais unidadeFuncional, AinQuartos quarto, AinLeitos leito) {
		if(unidadeFuncional != null) {
			return unidadeFuncional.getSeq();
		} else if (quarto != null) {
			if (quarto.getUnidadeFuncional() != null) {
				return quarto.getUnidadeFuncional().getSeq();
			}
		} else if (leito != null) {
			if(leito.getUnidadeFuncional() != null) {
				return leito.getUnidadeFuncional().getSeq();
			}
		}
		
		return null;
	}

	/**
	 * Valida a data inicial e final e verifica se a diferença entre elas é
	 * superior ao permitido
	 * 
	 * @author Stanley Araujo
	 * @param dataInicio
	 *            - Data de inicio da validação
	 * @param dataFinal
	 *            - Data de final da validação
	 * @throws ApplicationBusinessException
	 * @exception ApplicationBusinessException
	 *                - Se a data inicial for menor que a data final
	 * @exception ApplicationBusinessException
	 *                - Se a diferenção entre a data final e a inicial for
	 *                superior ao limite permitido
	 * */
	public void validarDiferencaDataInicialFinalSemEspecialidade(Date dataInicio, Date dataFinal) throws ApplicationBusinessException {
		AghParametros parametroMaxDif = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MAX_GERAL_DIF_DATAS_RELATORIO);
		int limiteDias = parametroMaxDif.getVlrNumerico().intValue(); // diferença
																		// máxima
																		// de
																		// dias
																		// entre
																		// data
																		// inicial
																		// e
																		// data
																		// final

		validarDiferencaDatas(limiteDias, dataInicio, dataFinal);
	}

	/**
	 * Valida a data inicial e final e verifica se a diferença entre elas é
	 * superior ao permitido
	 * 
	 * @author Stanley Araujo
	 * @param dataInicio
	 *            - Data de inicio da validação
	 * @param dataFinal
	 *            - Data de final da validação
	 * @throws ApplicationBusinessException
	 * @exception ApplicationBusinessException
	 *                - Se a data inicial for menor que a data final
	 * @exception ApplicationBusinessException
	 *                - Se a diferenção entre a data final e a inicial for
	 *                superior ao limite permitido
	 * */
	public void validarDiferencaDataInicialFinalComEspecialidade(Date dataInicio, Date dataFinal) throws ApplicationBusinessException {
		AghParametros parametroMaxDif = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MAX_ESPECIALIDADE_DIF_DATAS_RELATORIO);
		int limiteDias = parametroMaxDif.getVlrNumerico().intValue(); // diferença
																		// máxima
																		// de
																		// dias
																		// entre
																		// data
																		// inicial
																		// e
																		// data
																		// final

		validarDiferencaDatas(limiteDias, dataInicio, dataFinal);
	}

	private void validarDiferencaDatas(int limiteDias, Date dataInicio, Date dataFinal) throws ApplicationBusinessException {

		// Testar com as datas sendo nulas
		// Testar com diferença superior a sete dias;
		//
		if (dataInicio.after(dataFinal)) { // verifica se a data inicial é
											// superior à data final
			throw new ApplicationBusinessException(PesquisaInternacaoRNExceptionCode.ERRO_DATA_INICIAL_MENOR_QUE_FINAL);
		}

		Calendar auxCal = Calendar.getInstance();
		auxCal.setTime(dataInicio);
		auxCal.add(Calendar.DAY_OF_MONTH, limiteDias);

		if (dataFinal.after(auxCal.getTime())) { // verifica se a diferença é no
													// máximo o número de dias
													// informado
			throw new ApplicationBusinessException(PesquisaInternacaoRNExceptionCode.ERRO_PERIODO_MAX_RELATORIOS, limiteDias);
		}

	}

	/***
	 * Valida os critérios de pesquisa de para Pesquisa Pacientes Admitidos
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código (chave) da especialidade
	 * @param codigoClinica
	 *            - Código (chave) da clínica
	 * @param codigoConvenio
	 *            - Código (chave) do convênio
	 * @param codigoPlano
	 *            - Código (chave) do plano
	 * @param dataInicial
	 *            - Data inicial para contagem
	 * @param dataFinal
	 *            - Data final para a contagem
	 * @param prontuario
	 *            - Número do prontuário do paciente
	 * @exception ApplicationBusinessException
	 *                - Se a especialidade é inválida
	 * @exception ApplicationBusinessException
	 *                - Se a clínica é inválida
	 * @exception ApplicationBusinessException
	 *                - Se o convênio é inválido
	 * @exception ApplicationBusinessException
	 *                - Se o plano é inválido
	 * @exception ApplicationBusinessException
	 *                - Se o número do prontuário é inválido
	 * @exception ApplicationBusinessException
	 *                - Se a data inicial for menor que a data final
	 * @exception ApplicationBusinessException
	 *                - Se a diferenção entre a data final e a inicial for
	 *                superior a 07(sete) dias
	 * @exception ApplicationBusinessException
	 *                - Se a diferenção entre a data final e a inicial for
	 *                superior a 31(trinta e um) dias
	 * */

	public void validaPesquisaPacientesAdmitidos(AghEspecialidades codigoEspecialidade, AghClinicas codigoClinica, Short codigoConvenio,
			Byte codigoPlano, Date dataInicial, Date dataFinal, Integer prontuario) throws ApplicationBusinessException {

		if (codigoEspecialidade != null) {

			if (this.getCadastrosBasicosInternacaoFacade().obterEspecialidade(codigoEspecialidade.getSeq()) == null) {
				throw new ApplicationBusinessException(PesquisaInternacaoRNExceptionCode.ERRO_ESPECIALIDADE_INVALIDA);
			}
		}

		if (codigoClinica != null) {

			if (this.getAghuFacade().obterClinica(codigoClinica.getCodigo()) == null) {
				throw new ApplicationBusinessException(PesquisaInternacaoRNExceptionCode.ERRO_CLINICA_INVALIDA);
			}
		}

		if (codigoConvenio != null || codigoPlano != null) {

			if (codigoConvenio == null) {
				throw new ApplicationBusinessException(PesquisaInternacaoRNExceptionCode.ERRO_CONVENIO_PLANO_INVALIDO);
			}

			if (codigoPlano == null) {
				throw new ApplicationBusinessException(PesquisaInternacaoRNExceptionCode.ERRO_CONVENIO_PLANO_INVALIDO);
			}

			if (this.getFaturamentoApoioFacade().obterPlanoPorId(codigoPlano.byteValue(), codigoConvenio.shortValue()) == null) {
				throw new ApplicationBusinessException(PesquisaInternacaoRNExceptionCode.ERRO_CONVENIO_PLANO_INVALIDO);
			}

		}

		if (prontuario != null) {

			if (this.getPacienteFacade().pesquisarPacientePorProntuario(prontuario) == null) {
				throw new ApplicationBusinessException(PesquisaInternacaoRNExceptionCode.LABEL_INFORMAR_PRONTUARIO_VALIDO);
			}

		}

		if (codigoEspecialidade != null) {
			this.validarDiferencaDataInicialFinalComEspecialidade(dataInicial, dataFinal);
		} else {
			this.validarDiferencaDataInicialFinalSemEspecialidade(dataInicial, dataFinal);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AinDiariasAutorizadasDAO getAinDiariasAutorizadasDAO() {
		return ainDiariasAutorizadasDAO;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}

	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IFaturamentoApoioFacade getFaturamentoApoioFacade() {
		return this.faturamentoApoioFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
