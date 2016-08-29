package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipDistritoSanitarios;
import br.gov.mec.aghu.paciente.dao.AipCidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipDistritoSanitariosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


@Stateless
public class CidadeCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CidadeCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipDistritoSanitariosDAO aipDistritoSanitariosDAO;

@Inject
private AipCidadesDAO aipCidadesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 436073230649775776L;

	private enum CidadeCRUDExceptionCode implements BusinessExceptionCode {
		AIP_DADOS_CIDADE_OBRIGATORIO,
		AIP_CEPFINAL_CIDADE_INVALIDO, 
		ERRO_REMOVER_CIDADE, ERRO_COD_IBGE_JA_EXISTE,
		ERRO_CIDADES_COM_MESMO_CEP,
		ERRO_CIDADE_COM_CEP_JA_EXISTE,
		ERRO_REMOVER_CIDADE_COM_FORNECEDORES, ERRO_REMOVER_CIDADE_COM_PESSOAS_FISICAS,
		ERRO_REMOVER_CIDADE_COM_POSTO_SAUDES, ERRO_REMOVER_CIDADE_COM_PACIENTES,
		ERRO_REMOVER_CIDADE_COM_LOGRADOUROS, ERRO_REMOVER_CIDADE_COM_ENDERECOS_PACIENTES,
		ERRO_REMOVER_CIDADE_COM_DISTR_SANITARIOS, ERRO_REMOVER_CIDADE_COM_INSTITUICOES_HOSPITALARES,
		ERRO_REMOVER_CIDADE_COM_DOADORES, ERRO_REMOVER_CIDADE_COM_BANCOS_DE_SANGUE,
		ERRO_REMOVER_CIDADE_COM_NOTIFICACAO_TBS;
	}
	
	

	/**
	 * Método para incluir/atualizar um objeto de cidade
	 */
	public void persistirCidade(AipCidades cidade, List<AipDistritoSanitarios> distritos) throws ApplicationBusinessException {
		existeCidadeComCodIbgeOuComCEP(cidade);
		validarDadosCidade(cidade);
		
		if (cidade.getCodigo() == null) {
			getAipCidadesDAO().persistir(cidade);
		} else{
			this.getAipCidadesDAO().merge(cidade);			
		}
		
		atualizarDistritoSanitario(cidade, distritos);
	}
	
	private void atualizarDistritoSanitario(AipCidades cidade, List<AipDistritoSanitarios> distritos) {
		if(!cidade.getAipDistritoSanitarios().isEmpty()){
			cidade.getAipDistritoSanitarios().clear();
		}

		cidade.getAipDistritoSanitarios().addAll(distritos);
		getAipCidadesDAO().merge(cidade);			
	}

	/**
	 * Metodo para verifica se existe cidade cadastrada no BD com codigo IBGE informado no campo 
	 * "Código IBGE" da tela ou se existe cidade cadastrada com o CEP informado na tela
	 * 
	 * @param cidade
	 *  
	 * 
	 */
	public void existeCidadeComCodIbgeOuComCEP(AipCidades cidade) throws ApplicationBusinessException{
		
		AipCidades aipCidade = this.obterCidadePorCEP(cidade.getCep());
		if(aipCidade != null && (cidade.getCodigo() == null || !aipCidade.getCodigo().equals(cidade.getCodigo()))){
			throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_CIDADE_COM_CEP_JA_EXISTE);
		}
		
		if (cidade != null && cidade.getCodIbge() != null) {
			//Pesquisa cidade pelo código IBGE
			aipCidade = this.getAipCidadesDAO().obterCidadePorCodIbge(cidade.getCodIbge());
			
			//Compara codigo da cidade recebida por parametro (que pode ser nulo, caso seja uma nova cidade) 
			//e da cidade obtida na busca realizada no BD. Se os IDs forem diferentes, lança a exceção
			if (aipCidade != null && !aipCidade.getCodigo().equals(cidade.getCodigo())) {
				throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_COD_IBGE_JA_EXISTE);
			}
		}
	}

	
	/**
	 * Método para buscar a lista de Distritos Sanitarios de uma Cidade.
	 * A lista de Distritos Sanitários de uma Cidade pode não estar sincronizada com a 
	 * session do hibernate, assim é necessário fazer essa busca antes de persistir.
	 */
	public List<AipDistritoSanitarios> pesquisarDistritoSanitariosPorIds(AipCidades cidade) {
		
		List<Short> ids = montarArrayDeIdsDistritoSanitario(cidade);
		
		if (ids == null) {
			return null;
		} else {

			List<AipDistritoSanitarios> li = getAipDistritoSanitariosDAO()
					.pesquisarDistritoSanitariosPorCodigos(ids);
			
			return li;
		}
	}
	
	/**
	 * Método para colocar todos os IDs de Distrito Sanitário dentro de um array. 
	 */
	private List<Short> montarArrayDeIdsDistritoSanitario(AipCidades cidade) {
		if (cidade.getAipDistritoSanitarios() == null || cidade.getAipDistritoSanitarios().size() == 0) {
			return null;
		} else {
			List<Short> ids = new ArrayList<Short>();
			for (AipDistritoSanitarios ds : cidade.getAipDistritoSanitarios()) {
				ids.add(ds.getCodigo());
			}
			return ids;		
		}
	}
	
	/**
	 * Método para validar campos nulos e demais regras para a inserção de uma cidade
	 */
	private void validarDadosCidade(AipCidades cidade) throws ApplicationBusinessException {
		if (cidade.getNome() == null || "".equalsIgnoreCase(cidade.getNome().trim())
				|| cidade.getAipUf() == null || cidade.getAipUf().getSigla() == null) {
			throw new ApplicationBusinessException(CidadeCRUDExceptionCode.AIP_DADOS_CIDADE_OBRIGATORIO);
		}
		
		if (cidade.getCepFinal() != null && cidade.getCep() > cidade.getCepFinal()) {
			throw new ApplicationBusinessException(CidadeCRUDExceptionCode.AIP_CEPFINAL_CIDADE_INVALIDO);
		}
	}
	

	
	
	/**
	 * Método para excluir uma cidade
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void excluirCidade(Integer codigo) throws ApplicationBusinessException {
		try { 
			AipCidades cidade = getAipCidadesDAO().obterPorChavePrimaria(codigo);
			getAipCidadesDAO().remover(cidade);
			flush();
		} catch (PersistenceException e) {
			LOG.error("Exceção capturada: ", e);
			Throwable cause = e.getCause();
			
			if (cause != null && cause instanceof ConstraintViolationException) {
				ConstraintViolationException ecv = (ConstraintViolationException) cause;
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(), "SCO_FRN_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_FORNECEDORES.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"RAP_PES_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_PESSOAS_FISICAS.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"MPM_PSS_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_POSTO_SAUDES.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"AIP_PAC_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_PACIENTES.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"AIP_LGR_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_LOGRADOUROS.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"AIP_ENP_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_ENDERECOS_PACIENTES.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"AIP_CDD_DST_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_DISTR_SANITARIOS.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"AGH_IHO_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_INSTITUICOES_HOSPITALARES.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"ABS_CAD_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_DOADORES.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"ABS_BSA_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_BANCOS_DE_SANGUE.toString(), Severity.ERROR);
				}
				
				if (StringUtils.containsIgnoreCase(ecv.getLocalizedMessage(),"MPM_NTB_CDD_FK1")) {
					throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE_COM_NOTIFICACAO_TBS.toString(), Severity.ERROR);
				}
			} else {
				throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_REMOVER_CIDADE.toString(),Severity.ERROR);
			}
		}

	}
	
	/**
	 * Método para obter uma cidade através do seu CEP
	 *  
	 */
	public AipCidades obterCidadePorCEP(Integer cep) throws ApplicationBusinessException  {
	
		AipCidades retorno = null;
		List<AipCidades> listaCidades = getAipCidadesDAO().obterCidadePorCEP(cep);
			
		if (listaCidades.size() > 1){
			throw new ApplicationBusinessException(CidadeCRUDExceptionCode.ERRO_CIDADES_COM_MESMO_CEP);			
		}
		else if (listaCidades.size() == 1){
			retorno = listaCidades.get(0);
		}
		
		return retorno;
	}
	

	protected AipCidadesDAO getAipCidadesDAO(){
		return aipCidadesDAO;
	}
	
	protected AipDistritoSanitariosDAO getAipDistritoSanitariosDAO(){
		return aipDistritoSanitariosDAO;
	}
}
