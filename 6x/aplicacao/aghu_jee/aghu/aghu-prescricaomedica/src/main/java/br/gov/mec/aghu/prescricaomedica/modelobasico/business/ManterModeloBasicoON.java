package br.gov.mec.aghu.prescricaomedica.modelobasico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.MpmItemModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoDieta;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoMedicamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoModoUsoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModeloBasicoProcedimentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * 
 * @author cvagheti
 * 
 */
@Stateless
public class ManterModeloBasicoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterModeloBasicoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmModeloBasicoMedicamentoDAO mpmModeloBasicoMedicamentoDAO;
	
	@Inject
	private MpmModeloBasicoCuidadoDAO mpmModeloBasicoCuidadoDAO;
	
	@Inject
	private MpmItemModeloBasicoDietaDAO mpmItemModeloBasicoDietaDAO;
	
	@Inject
	private MpmModeloBasicoModoUsoProcedimentoDAO mpmModeloBasicoModoUsoProcedimentoDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MpmModeloBasicoPrescricaoDAO mpmModeloBasicoPrescricaoDAO;
	
	@Inject
	private MpmModeloBasicoProcedimentoDAO mpmModeloBasicoProcedimentoDAO;
	
	@Inject
	private MpmItemModeloBasicoMedicamentoDAO mpmItemModeloBasicoMedicamentoDAO;
	
	@Inject
	private MpmModeloBasicoDietaDAO mpmModeloBasicoDietaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5431823341492984947L;

	public enum ManterModeloBasicoONExceptionCode implements
			BusinessExceptionCode {
		/**
		 * Não pode ser excluido pois possui itens associados.
		 */
		MENSAGEM_MODELO_BASICO_POSSUI_ITENS_ASSOCIADOS, //
		/**
		 * MPM-00965 - Somente o servidor exclusivo pode excluir.
		 */
		MENSAGEM_MODELO_BASICO_SOMENTE_SERVIDOR_EXCLUSIVO_EXCLUI, //
		/**
		 * Somente o servidor exclusivo pode alterar.
		 */
		MENSAGEM_MODELO_BASICO_SOMENTE_SERVIDOR_EXCLUSIVO_ALTERA,
		/**
		 * Não permite inserir descrição vazia
		 */
		MENSAGEM_MODELO_BASICO_ERRO_PARAMETRO_OBRIGATORIO;
	}

	public void incluir(MpmModeloBasicoPrescricao modelo)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if ((modelo == null) || StringUtils.isBlank(modelo.getDescricao())) {
			throw new ApplicationBusinessException(
					ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_ERRO_PARAMETRO_OBRIGATORIO);
		}

		try {
			modelo.setServidorDigitado(servidorLogado);
			modelo.setServidor(servidorLogado);
			modelo.setCriadoEm(Calendar.getInstance().getTime());
			this.getMpmModeloBasicoPrescricaoDAO().persistir(modelo);
			this.getMpmModeloBasicoPrescricaoDAO().flush();
		} catch (BaseRuntimeException e) {
			throw new ApplicationBusinessException(e.getCode());
		}
	}

	/**
	 * Exclui modelo básico da prescrição.
	 * 
	 * @param modelo
	 * @throws ApplicationBusinessException
	 *             sem existir itens associados
	 */
	public void excluirModeloBasico(Integer modeloBasicoSeq)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (modeloBasicoSeq == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}
		
		MpmModeloBasicoPrescricao modelo = this.getMpmModeloBasicoPrescricaoDAO().obterPorChavePrimaria(modeloBasicoSeq);


		if (!servidorLogado.equals(modelo.getServidor())) {
			throw new ApplicationBusinessException(
					ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_SOMENTE_SERVIDOR_EXCLUSIVO_EXCLUI);
		}

		try {
			this.getMpmModeloBasicoPrescricaoDAO().remover(modelo);
			this.getMpmModeloBasicoPrescricaoDAO().flush();
		} catch (PersistenceException e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				throw new ApplicationBusinessException(
						ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_POSSUI_ITENS_ASSOCIADOS,
						e);
			}
			throw e;

		}

	}

	public void alterar(MpmModeloBasicoPrescricao modelo)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (modelo == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		if (!servidorLogado.equals(modelo.getServidor())) {
			throw new ApplicationBusinessException(
					ManterModeloBasicoONExceptionCode.MENSAGEM_MODELO_BASICO_SOMENTE_SERVIDOR_EXCLUSIVO_ALTERA);
		}

		modelo.setDescricao(modelo.getDescricao().toUpperCase());

		this.getMpmModeloBasicoPrescricaoDAO().merge(modelo);
		this.getMpmModeloBasicoPrescricaoDAO().flush();
	}

	public List<MpmModeloBasicoPrescricao> listarModelosBasicos() {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();		
		return this.getMpmModeloBasicoPrescricaoDAO().listar(servidorLogado);
	}
	
	/**
	 * Retorna os modelos básicos públicos
	 * @return
	 */
	public List<MpmModeloBasicoPrescricao> pequisarModelosPublicos(String descricaoModelo, String descricaoCentroCusto, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		return this.getMpmModeloBasicoPrescricaoDAO().pequisarModelosPublicos(descricaoModelo, descricaoCentroCusto, firstResult, maxResult, orderProperty, asc);
		
	}
	
	/**
	 * Retorna quantidade de modelos básicos públicos
	 * @return
	 */
	public Long pequisarModelosPublicosCount(String descricaoModelo, String descricaoCentroCusto) {
		
		return this.getMpmModeloBasicoPrescricaoDAO().pequisarModelosPublicosCount(descricaoModelo, descricaoCentroCusto);
		
	}
	
	/**
	 * Copia o modelo básico selecionado para o usuário logado.
	 * @param seq
	 * @param servidor
	 *  
	 */
	public void copiarModeloBasico(Integer seq) throws BaseException {
		
		RapServidores servidor = getServidorLogadoFacade().obterServidorLogado();
		
		MpmModeloBasicoPrescricao modeloBasico = this.getMpmModeloBasicoPrescricaoDAO().obterModeloBasicoPrescricaoPeloId(seq);
		
		if (modeloBasico != null) {
			
			MpmModeloBasicoPrescricao modeloCopia = new MpmModeloBasicoPrescricao();
			modeloCopia.setCriadoEm(new Date());
			modeloCopia.setDescricao(modeloBasico.getDescricao());
			modeloCopia.setServidor(servidor);
			modeloCopia.setServidorDigitado(servidor);
			modeloCopia.setIndPublico(false);
			
			this.getMpmModeloBasicoPrescricaoDAO().persistir(modeloCopia);
			this.getMpmModeloBasicoPrescricaoDAO().flush();
			
			copiarCuidados(modeloBasico, modeloCopia);
			copiarProcedimentos(modeloBasico, modeloCopia);
			copiarDietas(modeloBasico, modeloCopia);
			copiarMedicamentos(modeloBasico, modeloCopia);
			
		} 
		
	}
	
	/**
	 * copia os cuidados do modelo basico selecionado
	 * @param modeloBasico
	 * @param modeloCopia
	 */
	private void copiarCuidados(MpmModeloBasicoPrescricao modeloBasico, MpmModeloBasicoPrescricao modeloCopia) {
		
		List<MpmModeloBasicoCuidado> cuidados = this.getMpmModeloBasicoCuidadoDAO().listar(modeloBasico);
		
		for (MpmModeloBasicoCuidado modeloBasicoCuidado : cuidados) {
			
			modeloBasicoCuidado.setModeloBasicoPrescricao(modeloCopia);
			modeloBasicoCuidado.setServidor(modeloCopia.getServidor());
			this.getMpmModeloBasicoCuidadoDAO().desatachar(modeloBasicoCuidado);
			this.getMpmModeloBasicoCuidadoDAO().persistir(modeloBasicoCuidado);
			this.getMpmModeloBasicoCuidadoDAO().flush();
			
		}
	}
	
	/**
	 * copia os procedimentos do modelo basico selecionado
	 * @param modeloBasico
	 * @param modeloCopia
	 */
	private void copiarProcedimentos(MpmModeloBasicoPrescricao modeloBasico, MpmModeloBasicoPrescricao modeloCopia) {
		
		List<MpmModeloBasicoProcedimento> procedimentos = this.getMpmModeloBasicoProcedimentoDAO().pesquisar(modeloBasico);
		
		for (MpmModeloBasicoProcedimento modeloBasicoProcedimento : procedimentos) {
			
			List<MpmModeloBasicoModoUsoProcedimento> listaModoUso = this.getMpmModeloBasicoModoUsoProcedimentoDAO().pesquisar(modeloBasicoProcedimento);

			modeloBasicoProcedimento.setModeloBasicoPrescricao(modeloCopia);
			modeloBasicoProcedimento.setServidor(modeloCopia.getServidor());
			
			this.getMpmModeloBasicoProcedimentoDAO().desatachar(modeloBasicoProcedimento);
			this.getMpmModeloBasicoProcedimentoDAO().persistir(modeloBasicoProcedimento);
			this.getMpmModeloBasicoProcedimentoDAO().flush();
			
			for (MpmModeloBasicoModoUsoProcedimento modeloBasicoModoUsoProcedimento : listaModoUso) {
				
				modeloBasicoModoUsoProcedimento.setModeloBasicoProcedimento(modeloBasicoProcedimento);
				modeloBasicoModoUsoProcedimento.setServidor(modeloBasicoProcedimento.getServidor());
				
				this.getMpmModeloBasicoModoUsoProcedimentoDAO().desatachar(modeloBasicoModoUsoProcedimento);
				this.getMpmModeloBasicoModoUsoProcedimentoDAO().persistir(modeloBasicoModoUsoProcedimento);
				this.getMpmModeloBasicoModoUsoProcedimentoDAO().flush();
				
			}
			
		}
		
	}
	
	/**
	 * copia as dietas do modelo basico selecionado
	 * @param modeloBasico
	 * @param modeloCopia
	 */
	private void copiarDietas(MpmModeloBasicoPrescricao modeloBasico, MpmModeloBasicoPrescricao modeloCopia) {
		
		List<MpmModeloBasicoDieta> listaDietas = this.getMpmModeloBasicoDietaDAO().pesquisar(modeloBasico);
		
		for (MpmModeloBasicoDieta modeloBasicoDieta : listaDietas) {
			
			List<MpmItemModeloBasicoDieta> listaItemDietas = this.getMpmItemModeloBasicoDietaDAO().pesquisar(modeloBasicoDieta.getId().getModeloBasicoPrescricaoSeq(), modeloBasicoDieta.getId().getSeq());
			modeloBasicoDieta.setModeloBasicoPrescricao(modeloCopia);
			modeloBasicoDieta.setServidor(modeloCopia.getServidor());
			
			this.getMpmModeloBasicoDietaDAO().desatachar(modeloBasicoDieta);
			this.getMpmModeloBasicoDietaDAO().persistir(modeloBasicoDieta);
			this.getMpmModeloBasicoDietaDAO().flush();
			
			for (MpmItemModeloBasicoDieta itemModeloBasicoDieta : listaItemDietas) {
				
				itemModeloBasicoDieta.setCriadoEm(new Date());
				itemModeloBasicoDieta.setModeloBasicoDieta(modeloBasicoDieta);
				itemModeloBasicoDieta.setServidor(modeloBasicoDieta.getServidor());

				this.getMpmItemModeloBasicoDietaDAO().desatachar(itemModeloBasicoDieta);
				this.getMpmItemModeloBasicoDietaDAO().persistir(itemModeloBasicoDieta);
				this.getMpmItemModeloBasicoDietaDAO().flush();
				
			}
			
		}
		
	}
	
	/**
	 * copia os medicamentos do modelo basico selecionado
	 * @param modeloBasico
	 * @param modeloCopia
	 */
	private void copiarMedicamentos(MpmModeloBasicoPrescricao modeloBasico, MpmModeloBasicoPrescricao modeloCopia) {
		
		List<MpmModeloBasicoMedicamento> listaMedicamentos = this.getMpmModeloBasicoMedicamentoDAO().pesquisar(modeloBasico);
		
		for (MpmModeloBasicoMedicamento modeloBasicoMedicamento : listaMedicamentos) {
			
			List<MpmItemModeloBasicoMedicamento> listaItensMedicamentos = this.getMpmItemModeloBasicoMedicamentoDAO().obterItensMedicamento(modeloBasicoMedicamento.getId().getModeloBasicoPrescricaoSeq(), modeloBasicoMedicamento.getId().getSeq());
			modeloBasicoMedicamento.setModeloBasicoPrescricao(modeloCopia);
			modeloBasicoMedicamento.setServidor(modeloCopia.getServidor());
			
			this.getMpmModeloBasicoMedicamentoDAO().desatachar(modeloBasicoMedicamento);
			this.getMpmModeloBasicoMedicamentoDAO().persistir(modeloBasicoMedicamento);
			this.getMpmModeloBasicoMedicamentoDAO().flush();
			
			for (MpmItemModeloBasicoMedicamento itemModeloBasicoMedicamento : listaItensMedicamentos) {
				
				itemModeloBasicoMedicamento.setModeloBasicoMedicamento(modeloBasicoMedicamento);
				itemModeloBasicoMedicamento.setServidor(modeloBasicoMedicamento.getServidor());
				this.getMpmItemModeloBasicoMedicamentoDAO().desatachar(itemModeloBasicoMedicamento);
				this.getMpmItemModeloBasicoMedicamentoDAO().persistir(itemModeloBasicoMedicamento);
				this.getMpmItemModeloBasicoMedicamentoDAO().flush();
				
			}
		}
		
	}

	// getters & setters

	protected MpmModeloBasicoPrescricaoDAO getMpmModeloBasicoPrescricaoDAO() {
		return mpmModeloBasicoPrescricaoDAO;
	}

	
	
	protected MpmModeloBasicoCuidadoDAO getMpmModeloBasicoCuidadoDAO() {
		return mpmModeloBasicoCuidadoDAO;
	}
	
	protected MpmModeloBasicoProcedimentoDAO getMpmModeloBasicoProcedimentoDAO() {
		return mpmModeloBasicoProcedimentoDAO;
	}
	
	protected MpmModeloBasicoModoUsoProcedimentoDAO getMpmModeloBasicoModoUsoProcedimentoDAO() {
		return mpmModeloBasicoModoUsoProcedimentoDAO;
	}
	
	protected MpmModeloBasicoDietaDAO getMpmModeloBasicoDietaDAO() {
		return mpmModeloBasicoDietaDAO;
	}
	
	protected MpmItemModeloBasicoDietaDAO getMpmItemModeloBasicoDietaDAO() {
		return mpmItemModeloBasicoDietaDAO;
	}
	
	protected MpmModeloBasicoMedicamentoDAO getMpmModeloBasicoMedicamentoDAO() {
		return mpmModeloBasicoMedicamentoDAO;
	}
	
	protected MpmItemModeloBasicoMedicamentoDAO getMpmItemModeloBasicoMedicamentoDAO() {
		return mpmItemModeloBasicoMedicamentoDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
