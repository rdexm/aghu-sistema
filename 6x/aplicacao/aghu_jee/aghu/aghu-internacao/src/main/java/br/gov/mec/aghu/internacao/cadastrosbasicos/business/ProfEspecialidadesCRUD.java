package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenioId;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinEscalasProfissionalIntId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de especialidades para proffisionais.
 */
@Stateless
public class ProfEspecialidadesCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory
			.getLog(ProfEspecialidadesCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade CadastrosBasicosInternacaoFacade;

	@EJB
	private IAghuFacade iAghuFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -566078829920665335L;

	private enum ProfEspecialidadesCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_ESPECIALIDADE_PROFISSIONAL_OBRIGATORIA, ERRO_PROFISSIONAL_OBRIGATORIO, ERRO_PERSISTIR_ESPECIALIDADE_PROFISSIONAL, ERRO_ESPECIALIDADE_JA_EXISTENTE;
	}

	/**
	 * @dbtables RapServidores select
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 */
	public List<RapServidores> pesquisarRapServidores(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer matricula, Integer vinculo, String nome) {
		return this.getRegistroColaboradorFacade().pesquisarRapServidores(
				firstResult, maxResults, orderProperty, asc, matricula,
				vinculo, nome);
	}

	/**
	 * @dbtables RapServidores select
	 * @param matricula
	 * @param vinculo
	 * @param nome
	 * @return
	 */
	public Long pesquisarRapServidoresCount(Integer matricula, Integer vinculo,
			String nome) {
		return this.getRegistroColaboradorFacade().pesquisarRapServidoresCount(
				matricula, vinculo, nome);
	}

	/**
	 * Método responsável pela persistência/atualização de uma especialidade para profissional.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void persistirProfEspecialidades(List<AghProfEspecialidades> profEspecialidades, RapServidoresId rapServidorId)
			throws ApplicationBusinessException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		RapServidores servidor = getRegistroColaboradorFacade().obterRapServidor(rapServidorId);
		List<AghProfEspecialidades> profEspecialidadesOld = getCadastrosBasicosInternacaoFacade().listarProfEspecialidadesPorServidor(servidor);
		
		try {
			IAghuFacade aghuFacade = this.getAghuFacade();

			for (AghProfEspecialidades profEspecialidade : profEspecialidades) {
				
				if(profEspecialidade.getServidorDigitador() == null){
					profEspecialidade.setServidorDigitador(servidorLogado);
				}
				
				// Verifica se objeto da lista sofreu alguma modificação
				if(profEspecialidadesOld.contains(profEspecialidade)) {
					AghProfEspecialidades profEspecialidadeOriginal = getAghuFacade().obterAghProfEspecialidadesPorChavePrimaria(profEspecialidade.getId());
					if(!(profEspecialidadeOriginal.getIndAtuaAmbt().equals(profEspecialidade.getIndAtuaAmbt()) &&
							profEspecialidadeOriginal.getIndAtuaInternacao().equals(profEspecialidade.getIndAtuaInternacao()) &&
									profEspecialidadeOriginal.getIndCirurgiaoBloco().equals(profEspecialidade.getIndCirurgiaoBloco()) &&
									profEspecialidadeOriginal.getIndInterna().equals(profEspecialidade.getIndInterna()) && 
									profEspecialidadeOriginal.getIndProfRealizaConsultoria().equals(profEspecialidade.getIndProfRealizaConsultoria()) &&
									profEspecialidadeOriginal.getCapacReferencial() == profEspecialidade.getCapacReferencial())) {	
						aghuFacade.mergeAghProfEspecialidades(profEspecialidade);
					}
				}
				else {
					aghuFacade.persistirAghProfEspecialidades(profEspecialidade);
				}
				
				List<AghProfissionaisEspConvenio> listaProfEspConv = aghuFacade.
						obterAghProfissionaisEspConvenioPorProfissionalEsp(
								profEspecialidade.getId().getSerMatricula(), profEspecialidade.getId().getSerVinCodigo(), profEspecialidade.getId().getEspSeq());
				
				if(listaProfEspConv.isEmpty()){
					AghProfissionaisEspConvenio profEspConv = new AghProfissionaisEspConvenio();
					profEspConv.setId(new AghProfissionaisEspConvenioId(Short.valueOf("1"), profEspecialidade.getId().getSerMatricula(), profEspecialidade.getId().getSerVinCodigo(), profEspecialidade.getId().getEspSeq()));
					profEspConv.setIndInterna(true);
					profEspConv.setIndSituacao(DominioSituacao.A);
					profEspConv.setIndAtuaCirurgiaoResponsavel(false);
					aghuFacade.persistirAghProfissionaisEspConvenio(profEspConv);
					
					List<AinEscalasProfissionalInt> escalas = internacaoFacade.pesquisarProfissionalEscala(profEspecialidade.getId().getSerMatricula(), 
					profEspecialidade.getId().getSerVinCodigo(), profEspConv.getId().getCnvCodigo(), profEspecialidade.getId().getEspSeq());
					
					if(escalas.isEmpty()){
						AinEscalasProfissionalInt escala = new AinEscalasProfissionalInt();
						escala.setId(new AinEscalasProfissionalIntId(profEspConv.getId().getCnvCodigo(), new Date(), profEspecialidade.getId().getSerMatricula(), 
						profEspecialidade.getId().getSerVinCodigo(), profEspecialidade.getId().getEspSeq()));
						escala.setIndAtuaCti("N");
						escala.setServidorProfessorDigitada(servidorLogado);
						internacaoFacade.incluirEscala(escala);
					}
				}
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(
					ProfEspecialidadesCRUDExceptionCode.ERRO_PERSISTIR_ESPECIALIDADE_PROFISSIONAL);
		}
	}

	/**
	 * Método responsável pelas validações dos dados de especialidade para
	 * proffisional. Método utilizado para inclusão e atualização de
	 * especialidade para proffisional.
	 * 
	 * @param especialidade
	 *            para proffisional
	 * @throws ApplicationBusinessException
	 */
	public void validarDados(AghProfEspecialidades profEspecialidades)
			throws ApplicationBusinessException {

		if (profEspecialidades.getId() == null || profEspecialidades.getId().getEspSeq() == null) {
			throw new ApplicationBusinessException(
					ProfEspecialidadesCRUDExceptionCode.ERRO_ESPECIALIDADE_PROFISSIONAL_OBRIGATORIA);
		}
		if (profEspecialidades.getId() == null || (profEspecialidades.getId().getSerMatricula() == null || profEspecialidades
						.getId().getSerVinCodigo() == null)) {
			throw new ApplicationBusinessException(
					ProfEspecialidadesCRUDExceptionCode.ERRO_PROFISSIONAL_OBRIGATORIO);
		}

	}

	/**
	 * Lista especialidades ATIVAS pela siga ou descricao ordenando os
	 * resultados pela sigla ou descricao.
	 * 
	 * @param paramPesquisa
	 *            - sigla ou descricao
	 * @return
	 */
	public List<AghEspecialidades> listarEspecialidadesAtivasPorSiglaOuDescricao(
			Object paramPesquisa, boolean ordemPorSigla) {
		return this.getAghuFacade().listarEspecialidadesPorSiglaOuDescricao(paramPesquisa, ordemPorSigla, true);
	}

	/**
	 * 
	 * Lista as Especialidades pela sigla ou nome
	 * 
	 * @dbtables AghEspecialidades select
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(
			Object strPesquisa) {
		return this.getAghuFacade().pesquisarEspecialidadePorSiglaNome(
				strPesquisa);
	}

	public Long pesquisarEspecialidadePorSiglaNomeCount(Object strPesquisa) {
		return this.getAghuFacade().pesquisarEspecialidadePorSiglaNomeCount(
				strPesquisa);
	}

	/**
	 * 
	 * Lista as Especialidades pela sigla
	 * 
	 * @dbtables AghEspecialidades select
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSigla(
			Object paramPesquisa) {
		return this.getAghuFacade().pesquisarEspecialidadesInternasPorSigla(
				paramPesquisa);
	}

	/**
	 * 
	 * Lista as Especialidades pela sigla e nome
	 * 
	 * @dbtables AghEspecialidades select
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidadesInternasPorSiglaENome(
			Object paramPesquisa) {
		return this.getAghuFacade()
				.pesquisarEspecialidadesInternasPorSiglaENome(paramPesquisa);
	}

	public Long pesquisarEspecialidadesInternasPorSiglaENomeCount(
			Object paramPesquisa) {
		return this.getAghuFacade()
				.pesquisarEspecialidadesInternasPorSiglaENomeCount(
						paramPesquisa);
	}
	
	/**
	 * Lista as especialidades pelo profissional
	 * @param usuarioResponsavel
	 * @return
	 */
	public List<AghProfEspecialidades> listarProfEspecialidadesPorServidor(RapServidores usuarioResponsavel){
		return this.getAghuFacade().listarProfEspecialidadesPorServidor(usuarioResponsavel);
	}

	/**
	 * @dbtables AghProfEspecialidades select
	 * @param aghProfEspecialidadesId
	 * @return
	 */
	public AghProfEspecialidades obterProfEspecialidades(
			AghProfEspecialidadesId aghProfEspecialidadesId) {
		AghProfEspecialidades retorno = this.getAghuFacade()
				.obterAghProfEspecialidadesPorChavePrimaria(
						aghProfEspecialidadesId);
		return retorno;
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.CadastrosBasicosInternacaoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
