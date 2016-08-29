package br.gov.mec.aghu.internacao.pesquisa.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.hibernate.Hibernate;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.BypassInactiveModule;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioGrupoConvenioPesquisaLeitos;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacComAlta;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacientesAdmitidos;
import br.gov.mec.aghu.dominio.DominioOrigemPaciente;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.dominio.DominioTipoAlta;
import br.gov.mec.aghu.internacao.administracao.business.VAinPesqPacOV;
import br.gov.mec.aghu.internacao.business.vo.InternacaoAtendimentoUrgenciaPacienteVO;
import br.gov.mec.aghu.internacao.dao.VAinDispVagasDAO;
import br.gov.mec.aghu.internacao.pesquisa.vo.CidInternacaoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.DadosInternacaoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ExtratoLeitoVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ExtratoPacienteVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaLeitosVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaPacientesAdmitidosVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisaPacientesComPrevisaoAltaVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.PesquisarSituacaoLeitosVO;
import br.gov.mec.aghu.internacao.pesquisa.vo.ReferencialEspecialidadeProfissonalGridVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.LeitoDisponibilidadeVO;
import br.gov.mec.aghu.internacao.vo.PacienteProfissionalEspecialidadeVO;
import br.gov.mec.aghu.internacao.vo.PesquisaReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.internacao.vo.QuartoDisponibilidadeVO;
import br.gov.mec.aghu.internacao.vo.VAinAltasVO;
import br.gov.mec.aghu.internacao.vo.VAinCensoVO;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinAcompanhantesInternacao;
import br.gov.mec.aghu.model.AinAcompanhantesInternacaoId;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinObservacoesCenso;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.VAinDispVagas;

/**
 * Porta de entrada do sub-módulo Pesquisa do módulo de Internação.
 * 
 * @author lcmoura
 * 
 */


@Modulo(ModuloEnum.INTERNACAO)
@SuppressWarnings({"PMD.ExcessiveClassLength"})
@Stateless
public class PesquisaInternacaoFacade extends BaseFacade implements IPesquisaInternacaoFacade {


@EJB
private PesquisarSituacaoLeitosON pesquisarSituacaoLeitosON;

@EJB
private PesquisaInternacaoRN pesquisaInternacaoRN;

@EJB
private PesquisaInternacaoON pesquisaInternacaoON;

@EJB
private DetalhaLeitoON detalhaLeitoON;

@EJB
private PesquisaReferencialEspecialidadeProfissionalON pesquisaReferencialEspecialidadeProfissionalON;

@EJB
private PesquisaPacientesProfissionalEspecialidadeON pesquisaPacientesProfissionalEspecialidadeON;

@EJB
private PesquisaReferencialClinicaEspecialidadeON pesquisaReferencialClinicaEspecialidadeON;

@EJB
private PesquisaReferencialEspecialidadeProfissionalRN pesquisaReferencialEspecialidadeProfissionalRN;

@EJB
private PesquisaPacienteInternadoON pesquisaPacienteInternadoON;

@EJB
private PesquisaLeitosON pesquisaLeitosON;

@EJB
private PesquisaExtratoLeitoON pesquisaExtratoLeitoON;

@EJB
private PesquisaExtratoPacienteON pesquisaExtratoPacienteON;

@EJB
private PesquisaCensoDiarioPacienteON pesquisaCensoDiarioPacienteON;

@Inject
private VAinDispVagasDAO vAinDispVagasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4057443508153638013L;

	@Override
	public boolean validar(Integer prontuario) throws ApplicationBusinessException {
		return getPesquisaExtratoPacienteON().validar(prontuario);
	}

	@Override
	public String getNomePacienteProntuario(Integer prontuario) {
		return getPesquisaExtratoPacienteON().getNomePacienteProntuario(prontuario);
	}

	@Override
	public List<DadosInternacaoVO> pesquisarDatas(Integer prontuario, Date dataInternacao) {
		return getPesquisaExtratoPacienteON().pesquisarDatas(prontuario, dataInternacao);
	}

	@Override
	public Long pesquisarExtratoPacienteCount(Integer codigoInternacao, Date dataInternacao) {
		return getPesquisaExtratoPacienteON().pesquisarExtratoPacienteCount(codigoInternacao, dataInternacao);
	}

	@Override
	public Boolean mostrarEstadoSaude(Short seqUnidadeFuncional) {
		return getPesquisaCensoDiarioPacienteON().mostrarEstadoSaude(seqUnidadeFuncional);
	}
	
	@Override
	@Secure("#{s:hasPermission('paciente','pesquisarExtrato')}")
	public List<ExtratoPacienteVO> pesquisarExtratoPaciente(Integer firstResult, Integer maxResult, Integer codigoInternacao,
			Date dataInternacao) {
		return getPesquisaExtratoPacienteON().pesquisarExtratoPaciente(firstResult, maxResult, codigoInternacao, dataInternacao);
	}
	
	/**
	 * Verifica se existe uma internação para o prontuario passado por parâmetro.
	 * @param numeroProntuario
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean verificarExisteInternacao(Integer numeroProntuario) throws ApplicationBusinessException {
		return this.getPesquisaInternacaoON().verificarExisteInternacao(numeroProntuario);
	}

	protected PesquisaExtratoPacienteON getPesquisaExtratoPacienteON() {
		return pesquisaExtratoPacienteON;
	}

	protected PesquisaInternacaoON getPesquisaInternacaoON() {
		return pesquisaInternacaoON;
	}

	protected PesquisaInternacaoRN getPesquisaInternacaoRN() {
		return pesquisaInternacaoRN;
	}

	protected PesquisaLeitosON getPesquisaLeitosON() {
		return pesquisaLeitosON;
	}

	protected PesquisarSituacaoLeitosON getPesquisarSituacaoLeitosON() {
		return pesquisarSituacaoLeitosON;
	}

	protected PesquisaExtratoLeitoON getPesquisaExtratoLeitoON() {
		return pesquisaExtratoLeitoON;
	}

	protected PesquisaPacienteInternadoON getPesquisaPacienteInternadoON() {
		return pesquisaPacienteInternadoON;
	}

	protected PesquisaPacientesProfissionalEspecialidadeON getPesquisaPacientesProfissionalEspecialidadeON() {
		return pesquisaPacientesProfissionalEspecialidadeON;
	}

	protected PesquisaReferencialClinicaEspecialidadeON getPesquisaReferencialClinicaEspecialidadeON() {
		return pesquisaReferencialClinicaEspecialidadeON;
	}

	protected PesquisaReferencialEspecialidadeProfissionalRN getPesquisaReferencialEspecialidadeProfissionalRN() {
		return pesquisaReferencialEspecialidadeProfissionalRN;
	}

	protected PesquisaCensoDiarioPacienteON getPesquisaCensoDiarioPacienteON() {
		return pesquisaCensoDiarioPacienteON;
	}
	
	protected DetalhaLeitoON getDetalhaLeitoON() {
		return detalhaLeitoON;
	}
	
	protected PesquisaReferencialEspecialidadeProfissionalON getPesquisaReferencialEspecialidadeProfissionalON() {
		return pesquisaReferencialEspecialidadeProfissionalON;
	}

	/**
	 * Método responsável por realizar a query de consulta de disponibilidades
	 * de leitos.
	 * 
	 * @param idAcomodacao
	 * @param idClinica
	 * @param seqUnidadeFuncional
	 * @param idLeito
	 * @param numeroQuarto
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<LeitoDisponibilidadeVO> pesquisarDisponibilidadeLeitos(Integer firstResult, Integer maxResult, Integer idAcomodacao,
			Integer idClinica, Short seqUnidadeFuncional, String idLeito, Short numeroQuarto) {
		return getPesquisaInternacaoON().pesquisarDisponibilidadeLeitos(firstResult, maxResult, idAcomodacao, idClinica,
				seqUnidadeFuncional, idLeito, numeroQuarto);
	}
	
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public Long pesquisarDisponibilidadeLeitosCount(Integer idAcomodacao,
			Integer idClinica, Short seqUnidadeFuncional, String idLeito, Short numeroQuarto) {
		return getPesquisaInternacaoON().pesquisarDisponibilidadeLeitosCount(idAcomodacao, idClinica,
				seqUnidadeFuncional, idLeito, numeroQuarto);
	}	

	/**
	 * Verifica se é possível internar paciente no leito informado em função do
	 * sexo
	 */
	@Override
	public void consistirSexoLeito(AipPacientes paciente, AinLeitos leito) throws ApplicationBusinessException {
		getPesquisaInternacaoON().consistirSexoLeito(paciente, leito);
	}

	/**
	 * Verifica se é possível internar paciente no quarto informado em função do
	 * sexo
	 */
	@Override
	public void consistirSexoQuarto(AipPacientes paciente, AinQuartos quarto) throws ApplicationBusinessException {
		getPesquisaInternacaoON().consistirSexoQuarto(paciente, quarto);
	}

	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public String buscarNomeUsual(Short pVinCodigo, Integer pMatricula) {
		return getPesquisaInternacaoON().buscarNomeUsual(pVinCodigo, pMatricula);
	}

	/**
	 * ORADB Function RAPC_BUSC_NRO_R_CONS
	 */
	@Override
	public String buscarNroRegistroConselho(Short vinCodigo, Integer matricula) {
		return getPesquisaInternacaoRN().buscarNroRegistroConselho(vinCodigo, matricula);
	}

	/**
	 * ORADB ainc_busca_senha_int
	 */
	@Override
	public String buscaSenhaInternacao(Integer internacaoSeq) {
		return getPesquisaInternacaoRN().buscaSenhaInternacao(internacaoSeq);
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
	@Override
	@BypassInactiveModule
	public DominioSimNao verificarCaracteristicaDaUnidadeFuncional(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		return getPesquisaInternacaoRN().verificarCaracteristicaDaUnidadeFuncional(unfSeq, caracteristica);
	}

	/**
	 * Retorna uma AinInternacao com base na chave primária.
	 * 
	 * @param seq
	 * @return AinInternacao
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacao(Integer seq) {
		return getPesquisaInternacaoON().obterInternacao(seq);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoComLefts(Integer seq) {
		return getPesquisaInternacaoON().obterInternacaoComLefts(seq);
	}
	
	/**
	 * Retorna os acompanhantes na internação.
	 * 
	 * @param seq
	 *            da Internação.
	 * @return Lista de Acomponhantes
	 */
	@Override
	@Secure("#{s:hasPermission('acompanhante','pesquisar')}")
	public List<AinAcompanhantesInternacao> obterAcompanhantesInternacao(Integer seq) {
		return getPesquisaInternacaoON().obterAcompanhantesInternacao(seq);
	}

	@Override
	public AinAcompanhantesInternacao obterAinAcompanhantesInternacao(AinAcompanhantesInternacaoId id){
		return getPesquisaInternacaoON().obterAinAcompanhantesInternacao(id);
	}

	/**
	 * Método para buscar a última internação de um paciente através do código
	 * do paciente.
	 * 
	 * @param codigoPaciente
	 * @return objeto AinInternacao
	 */
	// @Secure
	@Override
	public AinInternacao obterInternacaoPacientePorCodPac(Integer codigoPaciente) {
		return getPesquisaInternacaoON().obterInternacaoPacientePorCodPac(codigoPaciente);
	}

	/**
	 * Método para buscar a internacação de um paciente que esteja internado
	 * (indPacienteInternado='S'). Caso o paciente não esteja internado,
	 * retornará null.
	 * 
	 * @param codigoPaciente
	 * @return objeto AinInternacao
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinInternacao obterInternacaoPacienteInternado(Integer codigoPaciente) {
		
		AinInternacao dadosInternacao = getPesquisaInternacaoON().obterInternacaoPacienteInternado(codigoPaciente);
		
		/* Inicializados para evitar LazyException */
		if (dadosInternacao != null){
			Hibernate.initialize(dadosInternacao.getPaciente());
			Hibernate.initialize(dadosInternacao.getLeito());
			Hibernate.initialize(dadosInternacao.getQuarto());
			Hibernate.initialize(dadosInternacao.getUnidadesFuncionais());
			Hibernate.initialize(dadosInternacao.getEspecialidade());
			Hibernate.initialize(dadosInternacao.getProjetoPesquisa());
			Hibernate.initialize(dadosInternacao.getConvenioSaudePlano());
			Hibernate.initialize(dadosInternacao.getServidorProfessor());
			Hibernate.initialize(dadosInternacao.getServidorProfessor()
					.getPessoaFisica());
			Hibernate.initialize(dadosInternacao.getServidorProfessor()
					.getPessoaFisica().getQualificacoes());
			Hibernate.initialize(dadosInternacao.getTipoCaracterInternacao());
			Hibernate.initialize(dadosInternacao.getOrigemEvento());
			Hibernate.initialize(dadosInternacao.getInstituicaoHospitalar());
			Hibernate.initialize(dadosInternacao.getCidsInternacao());
		}
		
		return dadosInternacao;
	}

	/**
	 * Localiza a unidade funcional associada a um quarto ou leito a partir dos
	 * parâmetros informados.
	 * 
	 * ORADB Function AINC_RET_UNF_SEQ.
	 * 
	 * @return Id da unidade funcional encontrada.
	 */
	@Override
	@BypassInactiveModule
	public Short aincRetUnfSeq(Short pUnfSeq, Short pQuarto, String pLeito) {
		return getPesquisaInternacaoRN().aincRetUnfSeq(pUnfSeq, pQuarto, pLeito);
	}
	
	/**
	 * Localiza a unidade funcional associada a um quarto ou leito a partir dos
	 * parâmetros informados.Sobrecarga do método para quando já se possui os
	 * objetos.
	 * 
	 * ORADB Function AINC_RET_UNF_SEQ.
	 * 
	 * @return Id da unidade funcional encontrada.
	 */
	@Override
	@BypassInactiveModule
	public Short aincRetUnfSeq(AghUnidadesFuncionais unidadeFuncional, AinQuartos quarto, AinLeitos leito) {
		return getPesquisaInternacaoRN().aincRetUnfSeq(unidadeFuncional, quarto, leito);
	}

	/**
	 * Método usado na pesquisa de disponibilidade de quartos.
	 */
	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<QuartoDisponibilidadeVO> pesquisarDisponibilidadeQuartosVO(Integer firstResult, Integer maxResult, Short nroQuarto,
			Integer clinica, Short unidade) {
		return getPesquisaInternacaoON().pesquisarDisponibilidadeQuartosVO(firstResult, maxResult, nroQuarto, clinica, unidade);
	}

	/**
	 * Método para retornar a capacidade dos quartos.
	 * 
	 * @param quartosList
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<QuartoDisponibilidadeVO> pesquisarCapacIntQrt(List<QuartoDisponibilidadeVO> quartosList) {
		return getPesquisaInternacaoON().pesquisarCapacIntQrt(quartosList);
	}

	/**
	 * Método que busca o total de pacientes internados por quarto.
	 * 
	 * @param quartoList
	 */
	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public void pesquisarTotalIntQrt(List<QuartoDisponibilidadeVO> quartoList) {
		getPesquisaInternacaoON().pesquisarTotalIntQrt(quartoList);
	}

	/**
	 * Busca SolicTransfPacientes associadas a quartos e com IndSolicLeito igual
	 * a P
	 * 
	 * @param quartoList
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public List<QuartoDisponibilidadeVO> pesquisarTotSolTransPndQrt(List<QuartoDisponibilidadeVO> quartoList) {
		return getPesquisaInternacaoON().pesquisarTotSolTransPndQrt(quartoList);
	}

	/**
	 * Busca vagas por quarto.
	 * 
	 * @param quartoList
	 */
	@Override
	@Secure("#{s:hasPermission('quarto','pesquisar')}")
	public void pesquisarDispVagasQrt(List<QuartoDisponibilidadeVO> quartoList) {
		getPesquisaInternacaoON().pesquisarDispVagasQrt(quartoList);
	}

	/**
	 * Verifica se o quarto pertence a uma unidade de internação ORADB:
	 * Procedure AINP_TRAZ_QRT da AINF_DISP_LEITOS.PLL ORADB: View
	 * V_AIN_QUARTOS_TRANSF
	 */
	@Override
	public void consistirQuarto(AinQuartos quarto) throws ApplicationBusinessException {
		getPesquisaInternacaoON().consistirQuarto(quarto);
	}

	/**
	 * Método para retornar todas Unidades Funcionais cadastradas que são
	 * unidade de internação.
	 * 
	 * @param Objeto
	 *            de Unidade Funcional para filtrar andar, ala e clínica
	 * @return Lista com todas Unidades Funcionais
	 */
	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public List<VAinDispVagas> pesquisarVAinDispVagas(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc, 
			  									 		   final AghClinicas clinica, final AghUnidadesFuncionais unidade){
		return getVAinDispVagasDAO().pesquisarVAinDispVagas(firstResult, maxResult, orderProperty, asc, clinica, unidade);
	}
	
	@Override
	@Secure("#{s:hasPermission('unidadeFuncional','pesquisar')}")
	public Long pesquisarVAinDispVagasCount(final AghClinicas clinica, final AghUnidadesFuncionais unidade){
		return getVAinDispVagasDAO().pesquisarVAinDispVagasCount(clinica, unidade);
	}
	
	protected VAinDispVagasDAO getVAinDispVagasDAO() {
		return vAinDispVagasDAO;
	}
	
	
	/**
	 * Método que pesquisa as internações de um dado paciente, filtrando pelo
	 * prontuário
	 * 
	 * @param prontuario
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacoesPorProntuarioUnidade(Integer prontuario) {
		return getPesquisaInternacaoON().pesquisarInternacoesPorProntuarioUnidade(prontuario);
	}

	/**
	 * Retorna uma view VAinPesqPac com base na prontuario que correspode uma
	 * internação.
	 * 
	 * @param prontuario
	 * @return VAinPesqPac
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public VAinPesqPacOV pesquisaDetalheInternacao(Integer intSeq) throws ApplicationBusinessException {
		return getPesquisaInternacaoON().pesquisaDetalheInternacao(intSeq);
	}

	@Override
	public Long pesquisaCidsInternacaoCount(Integer codInternacao) {
		return getPesquisaInternacaoON().pesquisaCidsInternacaoCount(codInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<CidInternacaoVO> pesquisaCidsInternacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer codInternacao) {
		return getPesquisaInternacaoON().pesquisaCidsInternacao(firstResult, maxResult, orderProperty, asc, codInternacao);
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

	@Override
	public void validaPesquisaPacientesAdmitidos(AghEspecialidades codigoEspecialidade,	AghClinicas codigoClinica, Short codigoConvenio,
			Byte codigoPlano, Date dataInicial, Date dataFinal, Integer prontuario) throws ApplicationBusinessException {
		getPesquisaInternacaoRN().validaPesquisaPacientesAdmitidos(codigoEspecialidade, codigoClinica, codigoConvenio, codigoPlano,
				dataInicial, dataFinal, prontuario);
	}

	/***
	 * Cria um DetachedCriteria de acordo com os parâmetros de pesquisa
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código(Chave) da especialidade
	 * @param origemPaciente
	 *            - Origem do Evento
	 * @param ordenacaoPesquisa
	 *            - Ordenação da pesquisa
	 * @param codigoClinica
	 *            - Código(cheve) da clínica
	 * @param codigoConvenio
	 *            - Código do convênio
	 * @param codigoPlano
	 *            - Código do plano associado ao convênio
	 * @param codigoPaciente
	 *            - Código do paciente
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @return Quantidade de pacientes admitidos
	 * */
	@Override
	public Long pesquisaPacientesAdmitidosCount(AghEspecialidades codigoEspecialidade, DominioOrigemPaciente origemPaciente,
			DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa, AghClinicas codigoClinica, Short codigoConveniosPlano,
			Byte codigoPlano, Date dataInicial, Date dataFinal, Integer codigoPaciente) {
		return getPesquisaInternacaoON().pesquisaPacientesAdmitidosCount(codigoEspecialidade, origemPaciente, ordenacaoPesquisa,
				codigoClinica, codigoConveniosPlano, codigoPlano, dataInicial, dataFinal, codigoPaciente);
	}

	/**
	 * Pesquisa os pacientes admitidos conforme os parâmetros informado.
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código(Chave) da especialidade
	 * @param origemPaciente
	 *            - Origem do Evento
	 * @param ordenacaoPesquisa
	 *            - Ordenação da pesquisa
	 * @param codigoClinica
	 *            - Código(cheve) da clínica
	 * @param codigoConvenio
	 *            - Código do convênio
	 * @param codigoPlano
	 *            - Código do plano associado ao convênio
	 * @param codigoPaciente
	 *            - Código do paciente
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @param firstResult
	 *            - Primeiro resultado
	 * @param maxResult
	 *            - Máximo resultado
	 * @param orderProperty
	 *            -
	 * @param asc
	 *            -
	 * 
	 * @return Lista de pacientes com previsão de alta
	 * 
	 * */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<PesquisaPacientesAdmitidosVO> pesquisaPacientesAdmitidos(AghEspecialidades codigoEspecialidade,
			DominioOrigemPaciente origemPaciente, DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa, AghClinicas codigoClinica,
			Short codigoConvenio, Byte codigoPlano, Date dataInicial, Date dataFinal, Integer codigoPaciente, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return getPesquisaInternacaoON().pesquisaPacientesAdmitidos(codigoEspecialidade, origemPaciente, ordenacaoPesquisa,
				codigoClinica, codigoConvenio, codigoPlano, dataInicial, dataFinal, codigoPaciente, firstResult, maxResult,
				orderProperty, asc);
	}

	@Override
	public void validaDefineWhere(Date dataInicio, Date dataFim, boolean altaAdministrativa, DominioTipoAlta tipoAlta, String tamCodigo)
			throws ApplicationBusinessException {
		getPesquisaInternacaoON().validaDefineWhere(dataInicio, dataFim, altaAdministrativa, tipoAlta, tamCodigo);
	}

	@Override
	public Long pesquisaPacientesComAltaCount(Date dataInicial, Date dataFinal, boolean altaAdministrativa,
			DominioTipoAlta tipoAlta, Short unidFuncSeq, Short espSeq, String tamCodigo) {
		return getPesquisaInternacaoON().pesquisaPacientesComAltaCount(dataInicial, dataFinal, altaAdministrativa, tipoAlta,
				unidFuncSeq, espSeq, tamCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	public List<VAinAltasVO> pesquisaPacientesComAlta(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Date dataInicial, Date dataFinal, boolean altaAdministrativa, DominioTipoAlta tipoAlta,
			DominioOrdenacaoPesquisaPacComAlta ordenacao, Short unidFuncSeq, Short espSeq, String tamCodigo) {
		return getPesquisaInternacaoON().pesquisaPacientesComAlta(firstResult, maxResult, orderProperty, asc, dataInicial, dataFinal,
				altaAdministrativa, tipoAlta, ordenacao, unidFuncSeq, espSeq, tamCodigo);
	}

	@Override
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public BigDecimal buscaMatriculaConvenio(Integer prontuario, Short convenio, Byte plano) {
		return getPesquisaInternacaoON().buscaMatriculaConvenio(prontuario, convenio, plano);
	}

	@Override
	public String montaLocalAltaDePaciente(String ltoLtoId, Short qrtNumero, String andar, AghAla indAla) {
		return getPesquisaInternacaoON().montaLocalAltaDePaciente(ltoLtoId, qrtNumero, andar, indAla);
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
	@Override
	public void validarDiferencaDataInicialFinalSemEspecialidade(Date dataInicio, Date dataFinal) throws ApplicationBusinessException {
		getPesquisaInternacaoRN().validarDiferencaDataInicialFinalSemEspecialidade(dataInicio, dataFinal);
	}

	/***
	 * Realiza a contagem de pacientes com previsão de alta em um intervalo de
	 * tempo
	 * 
	 * @author Stanley Araujo
	 * @param dataInicial
	 *            - Data inicial para contagem
	 * @param dataFinal
	 *            - Data final para a contagem
	 * @return Quantidade de pacientes com previsão de alta
	 * */
	@Override
	public Long pesquisaPacientesComPrevisaoAltaCount(Date dataInicial, Date dataFinal) {
		return getPesquisaInternacaoON().pesquisaPacientesComPrevisaoAltaCount(dataInicial, dataFinal);
	}

	/***
	 * Realiza a pesquisa de pacientes com previsão de alta em um intervalo de
	 * tempo
	 * 
	 * 
	 * @author Stanley Araujo
	 * @param firstResult
	 *            - Primeiro resultado
	 * @param maxResult
	 *            - Máximo resultado
	 * @param orderProperty
	 *            -
	 * @param asc
	 *            -
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @return Lista de pacientes com previsão de alta
	 * */
	@Override
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	public List<PesquisaPacientesComPrevisaoAltaVO> pesquisaPacientesComPrevisaoAlta(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Date dataInicial, Date dataFinal) {
		return getPesquisaInternacaoON().pesquisaPacientesComPrevisaoAlta(firstResult, maxResult, orderProperty, asc, dataInicial,
				dataFinal);
	}

	/**
	 * ORADB ainc_busca_ult_alta
	 */
	@Override
	public Date buscaUltimaAlta(Integer internacaoSeq) {
		return getPesquisaInternacaoRN().buscaUltimaAlta(internacaoSeq);
	}

	@Override
	public Long pesquisarInternacoesDoPacientePorProntuarioEDataInternacaoCount(Integer prontuario, Date dataInternacao) {
		return getPesquisaInternacaoON().pesquisarInternacoesDoPacientePorProntuarioEDataInternacaoCount(prontuario, dataInternacao);
	}

	/**
	 * Metodo que busca internacoes de um paciente atraves do seu prontuario
	 * (obrigatorio) e sua data de internacao (opcional).
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param prontuario
	 *            - prontuario do paciente o qual desejamos buscar as
	 *            internacoes.
	 * @param dataInternacao
	 *            - data de internacao do paciente
	 * @return lista de internacoes do respectivo prontuario informado como
	 *         parametro.
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacoesDoPacientePorProntuarioEDataInternacao(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, Integer prontuario, Date dataInternacao) {
		return getPesquisaInternacaoON().pesquisarInternacoesDoPacientePorProntuarioEDataInternacao(firstResult, maxResult,
				orderProperty, asc, prontuario, dataInternacao);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<InternacaoAtendimentoUrgenciaPacienteVO> pesquisarInternacaoAtendimentoUrgenciaPorPaciente(Integer codigoPaciente) {
		return getPesquisaInternacaoON().pesquisarInternacaoAtendimentoUrgenciaPorPaciente(codigoPaciente);
	}

	/**
	 * Método para buscar todos registros de internação com o ID do projeto de
	 * pesquisa recebido por parâmetro.
	 * 
	 * @param seqProjetoPesquisa
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacaoPorProjetoPesquisa(Integer seqProjetoPesquisa) {
		return getPesquisaInternacaoON().pesquisarInternacaoPorProjetoPesquisa(seqProjetoPesquisa);
	}

	/**
	 * Método para retornar todos objetos de internação de um determinado
	 * paciente
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacaoPorPaciente(Integer codigoPaciente) {
		return getPesquisaInternacaoON().pesquisarInternacaoPorPaciente(codigoPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public Boolean verificarPacienteInternado(Integer codigoPaciente) {
		return this.getPesquisaInternacaoON().verificarPacienteInternado(codigoPaciente);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public Boolean verificarPacienteHospitalDia(Integer codigoPaciente) {
		return this.getPesquisaInternacaoON().verificarPacienteHospitalDia(codigoPaciente);
	}

	@Override
	public void consistirSexoLeito(Integer codigoPaciente, String idLeito) throws ApplicationBusinessException {
		this.getPesquisaInternacaoON().consistirSexoLeito(codigoPaciente, idLeito);
	}

	@Override
	public void consistirSexoQuarto(Integer codigoPaciente, Short quartoNumero) throws ApplicationBusinessException {
		this.getPesquisaInternacaoON().consistirSexoQuarto(codigoPaciente, quartoNumero);
	}

	@Override
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AinAtendimentosUrgencia obterPacienteAtendimentoUrgencia(Integer codigoPaciente) {
		return this.getPesquisaInternacaoON().obterPacienteAtendimentoUrgencia(codigoPaciente);
	}

	/**
	 * retorna o detalhe de um atendimento de urgência.
	 * 
	 * @dbtables AinAtendimentosUrgencia select
	 * 
	 * @param codigoAtendimentoUrgencia
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AinAtendimentosUrgencia obterDetalheAtendimentoUrgencia(Integer codigoAtendimentoUrgencia) throws ApplicationBusinessException {
		return this.getPesquisaLeitosON().obterDetalheAtendimentoUrgencia(codigoAtendimentoUrgencia);
	}

	/**
	 * @param leito
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AinAtendimentosUrgencia obterDetalheAtendimentoUrgencia2(String leito) {
		return this.getPesquisaLeitosON().obterDetalheAtendimentoUrgencia2(leito);
	}

	/**
	 * Valida se pelo menos um filtro foi informado pela pesquisa.
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validaDados(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
							AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
							AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, 
							AghAla ala, Integer andar, DominioSimNao infeccao) throws ApplicationBusinessException {
		this.getPesquisaLeitosON().validaDados(status, acomodacao, clinica, convenio, unidade, leito, grupoConvenio, ala, andar, infeccao);
	}

	/**
	 * Verifica qual data block deve ser executado 0 - VPL 1 - VPL1 2 - STP
	 * 
	 * @return
	 */
	@Override
	public Integer verificarDataBlock(FatConvenioSaude convenio, DominioGrupoConvenioPesquisaLeitos grupoConvenio,
			DominioMovimentoLeito mvtoLeito) {
		return this.getPesquisaLeitosON().verificarDataBlock(convenio, grupoConvenio, mvtoLeito);
	}

	/**
	 * Retorna total de registros
	 * 
	 * @dbtables AinSolicTransfPacientes select
	 * 
	 * @return
	 */
	@Override
	public Long pesquisarSolicitacoesTransferenciaPacientesCount() {
		return this.getPesquisaLeitosON().pesquisarSolicitacoesTransferenciaPacientesCount();
	}

	/**
	 * Cria a pesquisa de acordo com o Data Block STP do oracle forms
	 * 
	 * @dbtables AinSolicTransfPacientes select
	 * 
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('solicitacaoTransferenciaPaciente','pesquisar')}")
	public List<PesquisaLeitosVO> pesquisarSolicitacoesTransferenciaPacientes() {
		return this.getPesquisaLeitosON().pesquisarSolicitacoesTransferenciaPacientes();
	}

	/**
	 * Método que obtém a lista de leitos.
	 * 
	 * @dbtables VAinPesqLeitos select
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<PesquisaLeitosVO> pesquisarLeitos(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala, Integer andar,
			DominioSimNao infeccao, DominioMovimentoLeito mvtoLeito, int dataBlock, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.getPesquisaLeitosON().pesquisarLeitos(status, acomodacao, clinica, convenio, unidade, leito, grupoConvenio, ala,
				andar, infeccao, mvtoLeito, dataBlock, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Retorna total de registros
	 * 
	 * @param status
	 * @param acomodacao
	 * @param clinica
	 * @param convenio
	 * @param unidade
	 * @param leito
	 * @param grupoConvenio
	 * @param ala
	 * @param andar
	 * @param infeccao
	 * @return
	 */
	@Override
	public Long pesquisarLeitosCount(AinTiposMovimentoLeito status, AinAcomodacoes acomodacao, 
			AghClinicas clinica, FatConvenioSaude convenio, AghUnidadesFuncionais unidade, 
			AinLeitos leito, DominioGrupoConvenioPesquisaLeitos grupoConvenio, AghAla ala, Integer andar, DominioSimNao infeccao,
			DominioMovimentoLeito mvtoLeito, int dataBlock) {
		return this.getPesquisaLeitosON().pesquisarLeitosCount(status, acomodacao, clinica, convenio, unidade, leito, grupoConvenio,
				ala, andar, infeccao, mvtoLeito, dataBlock);
	}

	@Override
	public Long pesquisaSituacaoLeitosCount(AghClinicas clinica) {
		return getPesquisarSituacaoLeitosON().pesquisaSituacaoLeitosCount(clinica);
	}

	@Override
	@Secure("#{s:hasPermission('leito','pesquisar')}")
	public List<PesquisarSituacaoLeitosVO> pesquisaSituacaoLeitos(AghClinicas clinicaParam, Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return getPesquisarSituacaoLeitosON().pesquisaSituacaoLeitos(clinicaParam, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long pesquisarExtratoLeitoCount(String leito, Date data) {
		return getPesquisaExtratoLeitoON().pesquisarExtratoLeitoCount(leito, data);
	}

	@Override
	@Secure("#{s:hasPermission('leito','pesquisarExtrato')}")
	public List<ExtratoLeitoVO> montarExtratoLeitoVO(String leito, Date data, Integer firstResult, Integer maxResult) {
		return getPesquisaExtratoLeitoON().montarExtratoLeitoVO(leito, data, firstResult, maxResult);
	}

	/**
	 * Pesquisa internacoes de pacientes que possuam o atributo indSaidaPaciente
	 * == false e tambem pelos seguintes parametros:
	 * 
	 * @param prontuario
	 * @param pacCodigo
	 * @param pacNome
	 * @param espSeq
	 *            - id da especialidade
	 * @param ltoId
	 *            - id do leito
	 * @param qrtNum
	 *            - numero do quarto
	 * @param unfSeq
	 *            - id da unidade funcional
	 * @param matriculaProfessor
	 * @param vinCodigoProfessor
	 * 
	 * @return List<AinInternacao> - lista de internacoes conforme os criterios
	 *         de pesquisa
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<AinInternacao> pesquisarInternacoesAtivas(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Integer prontuario, Integer pacCodigo, String pacNome, Short espSeq, String leitoID, Short qrtNum, Short unfSeq,
			Integer matriculaProfessor, Short vinCodigoProfessor) {

		return getPesquisaPacienteInternadoON().pesquisarInternacoesAtivas(firstResult, maxResults, orderProperty, asc, prontuario,
				pacCodigo, pacNome, espSeq, leitoID, qrtNum, unfSeq, matriculaProfessor, vinCodigoProfessor);
	}

	@Override
	public Long pesquisarInternacoesAtivasCount(Integer prontuario, Integer pacCodigo, String pacNome, Short espSeq,
			String leitoID, Short qrtNum, Short unfSeq, Integer matriculaProfessor, Short vinCodigoProfessor) {

		return getPesquisaPacienteInternadoON().pesquisarInternacoesAtivasCount(prontuario, pacCodigo, pacNome, espSeq, leitoID,
				qrtNum, unfSeq, matriculaProfessor, vinCodigoProfessor);
	}

	/**
	 * ORADB Function AINC_BUSCA_UNID_INT.
	 * 
	 * @param leitoID
	 * @param qrtNumero
	 * @param unfSeq
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public Short buscarUnidadeInternacao(String leitoID, Short qrtNumero, Short unfSeq) {
		return getPesquisaPacienteInternadoON().buscarUnidadeInternacao(leitoID, qrtNumero, unfSeq);
	}

	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public EspCrmVO pesquisarProfissionalPorEspecialidadeCRM(AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException {
		return getPesquisaPacientesProfissionalEspecialidadeON().pesquisarProfissionalPorEspecialidadeCRM(especialidade, strPesquisa);
	}

	/**
	 * 
	 * Busca especialidades por Nome ou Sigla
	 * 
	 * @return Lista de especialidades
	 */
	@Override
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(Object strPesquisa) {
		return getPesquisaPacientesProfissionalEspecialidadeON().pesquisarEspecialidadePorSiglaNome(strPesquisa);
	}

	@Override
	public Long pesquisarEspecialidadePorSiglaNomeCount(Object strPesquisa) {
		return getPesquisaPacientesProfissionalEspecialidadeON().pesquisarEspecialidadePorSiglaNomeCount(strPesquisa);
	}

	@Override
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<EspCrmVO> pesquisaProfissionalEspecialidade(AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException {
		return getPesquisaPacientesProfissionalEspecialidadeON().pesquisaProfissionalEspecialidade(especialidade, strPesquisa);
	}

	@Override
	public Integer pesquisaPacientesProfissionalEspecialidadeCount(AghEspecialidades especialidade, EspCrmVO profissional)
			throws ApplicationBusinessException {
		return getPesquisaPacientesProfissionalEspecialidadeON().pesquisaPacientesProfissionalEspecialidadeCount(especialidade,
				profissional);
	}

	@Override
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<PacienteProfissionalEspecialidadeVO> pesquisaPacientesProfissionalEspecialidade(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, AghEspecialidades especialidade, EspCrmVO profissional)
			throws ApplicationBusinessException {
		return getPesquisaPacientesProfissionalEspecialidadeON().pesquisaPacientesProfissionalEspecialidade(firstResult, maxResults,
				orderProperty, asc, especialidade, profissional);
	}

	/**
	 * Método para pesquisar escalar de profissionais.
	 * 
	 * @param matriculaProfessor
	 * @param codigoProfessor
	 * @param seqEspecialidade
	 * @param codigoConvenioSaude
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('escalaProfissionais','pesquisar')}")
	public List<AinEscalasProfissionalInt> pesquisarEscalaProfissionalInt(Integer matriculaProfessor, Short codigoProfessor,
			Short seqEspecialidade, Short codigoConvenioSaude) {
		return getPesquisaPacientesProfissionalEspecialidadeON().pesquisarEscalaProfissionalInt(matriculaProfessor, codigoProfessor,
				seqEspecialidade, codigoConvenioSaude);
	}

	@Override
	public void validaDadosPesquisaReferencialClinicaEspecialidade(AghClinicas clinica) throws ApplicationBusinessException {
		getPesquisaReferencialClinicaEspecialidadeON().validaDadosPesquisaReferencialClinicaEspecialidade(clinica);
	}

	@Override
	public Long pesquisaReferencialClinicaEspecialidadeCount(AghClinicas clinica) throws ApplicationBusinessException {
		return getPesquisaReferencialClinicaEspecialidadeON().pesquisaReferencialClinicaEspecialidadeCount(clinica);
	}

	/**
	 * ORADB V_AIN_PES_REF_CLI_ESP
	 * 
	 * Esta view realiza o join de 4 SQLs, os quais serão transcritos como SQLs
	 * independentes e adicionados a mesma coleção, paga unificar os resultados.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param clinica
	 * 
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('referencialClinicaEspecialidade','pesquisar')}")
	public List<PesquisaReferencialClinicaEspecialidadeVO> pesquisaReferencialClinicaEspecialidade(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AghClinicas clinica) throws ApplicationBusinessException {
		return getPesquisaReferencialClinicaEspecialidadeON().pesquisaReferencialClinicaEspecialidade(firstResult, maxResult,
				orderProperty, asc, clinica);
	}

	/**
	 * 
	 * @return List<VAinCensoVO> - lista de VAinCensoVO conforme os criterios de
	 *         pesquisa.
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('censoDiario','pesquisar')}")
	public Object[] pesquisarCensoDiarioPacientes(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status) throws ApplicationBusinessException {
		return getPesquisaCensoDiarioPacienteON().pesquisarCensoDiarioPacientes(firstResult, maxResults, orderProperty, asc, unfSeq,
				unfSeqMae, data, status);
	}

	@Override
	public Integer calcularIdadeDaData(Date data) {
		return getPesquisaCensoDiarioPacienteON().calcularIdadeDaData(data);
	}
	
	/**
	 * Verifica se uma determinada internacao possui documentos pendentes.
	 * 
	 * @param intSeq
	 *            - id da internacao
	 * @return true - se existirem docs pendentes
	 */
	@Override
	public boolean verificarDocumentosPendentes(Integer intSeq) {
		return getPesquisaCensoDiarioPacienteON().verificarDocumentosPendentes(intSeq);		
	}
	
	/**
	 * Obtem a observacaoCenso de uma internacao com data de criacao menor ou
	 * igual a data passada como parametro e também a partir do id da
	 * internacao.
	 * 
	 * @param intSeq
	 * @return AinObservacoesCenso
	 */
	@Override
	@Secure("#{s:hasPermission('observacoesCensoDiario','pesquisar')}")
	public AinObservacoesCenso obterObservacaoDaInternacao(Integer intSeq, Date data) {
		return getPesquisaCensoDiarioPacienteON().obterObservacaoDaInternacao(intSeq, data);
	}
	
	/**
	 * Inclui ou edita uma observacao.
	 * 
	 * @param observacaoCenso
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('observacoesCensoDiario','alterar')}")
	public void persistirObservacao(AinObservacoesCenso observacaoCenso) throws ApplicationBusinessException {
		getPesquisaCensoDiarioPacienteON().persistirObservacao(observacaoCenso);
	}
	
	/**
	 * 
	 * @param leito
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('leito','pesquisarExtrato')}")
	public AinExtratoLeitos obterUltimoExtratoLeito(String leito) {
		return getDetalhaLeitoON().obterUltimoExtratoLeito(leito);
	}
	
	@Override
	public void validaDadosPesquisaReferencialEspecialidadeProfissional(AghEspecialidades especialidade) throws ApplicationBusinessException {
		getPesquisaReferencialEspecialidadeProfissionalON().validaDadosPesquisaReferencialEspecialidadeProfissional(especialidade);
	}
	
	@Override
	public Long pesquisarReferencialEspecialidadeProfissonalGridVOCount(AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		return getPesquisaReferencialEspecialidadeProfissionalON().pesquisarReferencialEspecialidadeProfissonalGridVOCount(
				especialidade);
	}
	
	/**
	 * Conversão da pesquisa sobre a view V_AIN_PES_REF_ESP_PRO, utilizada na
	 * funcionalidade "Pesquisar referencial Especialidade/Profissional".
	 * 
	 * @return
	 */
	@Override
	@Secure("#{s:hasPermission('referencialEspecialidadeProfissional','pesquisar')}")
	public List<ReferencialEspecialidadeProfissonalGridVO> pesquisarReferencialEspecialidadeProfissonalGridVO(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AghEspecialidades especialidade) throws ApplicationBusinessException {
		return getPesquisaReferencialEspecialidadeProfissionalON().pesquisarReferencialEspecialidadeProfissonalGridVO(firstResult,
				maxResult, orderProperty, asc, especialidade);
	}
	
	/**
	 * Função pacienteNotifGMR Verifica se o paciente possui notificação de
	 * germe multirresistente. Chamar função: MCIC_PAC_NOTIF_GMR
	 * 
	 * @param Integer aipPacientesCodigo
	 * @param String sLogin
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Boolean pacienteNotifGMR(Integer aipPacientesCodigo){
		return getPesquisaCensoDiarioPacienteON().pacienteNotifGMR(aipPacientesCodigo);
	}

	public void excluirObservacaoDaInternacao(AinObservacoesCenso observacao) {
		getPesquisaCensoDiarioPacienteON().excluirObservacaoDaInternacao(observacao);
	}
	
	@Override
	public List<VAinCensoVO> pesquisarCensoDiarioPacientesSemPaginator(	Short unfSeq, Short unfSeqMae, Date data,
			DominioSituacaoUnidadeFuncional status)
			throws ApplicationBusinessException {
		
		return getPesquisaCensoDiarioPacienteON().pesquisarCensoDiarioPacientesSemPaginator(unfSeq, unfSeqMae, data, status);
	}

	@Override
	public AghUnidadesFuncionais obterUnidadeFuncional(Short unfSeq, Short unfSeqPai) {
		 return getPesquisaCensoDiarioPacienteON().obterUnidadeFuncional(unfSeq,
				unfSeqPai);
	}
	
	@Override
	public Integer pesquisarCensoDiarioPacientesCount(Short unfSeq, Short unfSeqMae, Date data, DominioSituacaoUnidadeFuncional status)
			throws ApplicationBusinessException{
		return getPesquisaCensoDiarioPacienteON().pesquisarCensoDiarioPacientesCount(unfSeq, unfSeqMae, data, status);
	}
}
