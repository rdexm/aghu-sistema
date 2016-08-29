package br.gov.mec.aghu.business;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.CoreUtil;

@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class EspecialidadeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(EspecialidadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghEspecialidadesDAO aghEspecialidadesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5635144328966201045L;

	/**
	 * Lista as especialidades pesquisando por sigla e descrição
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> listarPorSigla(Object paramPesquisa) {

		return executarPesquisaEspecialidades(paramPesquisa, false, false,
				false);
	}

	public List<AghEspecialidades> listarPorSiglaAtivas(Object paramPesquisa) {
		return executarPesquisaEspecialidades(paramPesquisa, false, false,
				true);
	}

	/**
	 * Lista apenas as especialidades ativas que permitem consultoria
	 * pesquisando por sigla e descrição.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<AghEspecialidades> listarPermitemConsultoriaPorSigla(
			Object paramPesquisa) {

		return executarPesquisaEspecialidades(paramPesquisa, true, false, true);
	}

	public List<AghEspecialidades> listarPermitemConsultoriaPorSigla(
			String paramPesquisa, boolean apenasSiglaSePossivel) {

		return executarPesquisaEspecialidades(paramPesquisa, true,
				apenasSiglaSePossivel, true);
	}

	public List<AghEspecialidades> listarEspecialidadePorSiglaENome(
			Object paramPesquisa) {

		return executarPesquisaEspecialidadesSolicitacaoInternacao(
				paramPesquisa, false);
	}

	public List<AghEspecialidades> listarEspecialidadeAtualizaSolicitacaoInternacao(
			Object paramPesquisa, Integer idade) {
		return executarPesquisaEspecialidadesSolicitacaoInternacao(
				paramPesquisa, false, idade);
	}

	public List<AghEspecialidades> listarEspecialidadeAtivasNaoInternas(
			Object paramPesquisa) {

		return executarPesquisaEspecialidadesAtivasNaoInternas(paramPesquisa,
				false);
	}

	public List<AghEspecialidades> listarEspecialidadeTransPaciente(
			Object paramPesquisa, Integer idade) {
		return executarPesquisaEspecialidadesTransPaciente(paramPesquisa, idade);
	}

	private List<AghEspecialidades> executarPesquisaEspecialidadesTransPaciente(Object paramPesquisa, Integer idade) {
		List<AghEspecialidades> lista = null;
		boolean flagExecutouPesquisa = false;

		String strPesquisa = (String) paramPesquisa;
		// Caso exista uma string de pesquisa,
		// busca pela sigla
		if (StringUtils.isNotBlank(strPesquisa)) {
			flagExecutouPesquisa = true;
			strPesquisa = strPesquisa.trim();
			lista = getAghEspecialidadesDAO().pesquisaEspecialidadesPorSiglaFaixaIdade(strPesquisa, idade);
		}

		// Caso exista uma string de pesquisa e a lista esteja vazia (nao tenha
		// encontrado resultados pela sigla),
		// pesquisa pelo nome da especialidade
		if (StringUtils.isNotBlank(strPesquisa) && (lista == null || lista.isEmpty())) {
			lista = getAghEspecialidadesDAO().pesquisaEspecialidadesPorNomeFaixaIdade(strPesquisa, idade);
		} else {
			// Caso nao tenha digitado algo na suggestion:
			if (!flagExecutouPesquisa) {
				lista = getAghEspecialidadesDAO().pesquisaEspecialidadesPorFaixaIdade(idade.shortValue());
			}
		}
		if (lista != null) {
			for (AghEspecialidades especialidade : lista) {
				Hibernate.initialize(especialidade.getClinica());
			}
		}
		return lista;
	}
	
	private List<AghEspecialidades> executarPesquisaEspecialidadesAtivasNaoInternas(Object paramPesquisa, boolean apenasSiglaSePossivel) {

		String strPesquisa = (String) paramPesquisa;

		List<AghEspecialidades> result = getAghEspecialidadesDAO().pesquisaEspecialidadesAtivasNaoInternasPorSigla(strPesquisa, 25);

		// Se tem o que pesquisar vai por descricao tambem mantendo a ordem da
		// pesquisa anterior sem repetir registros
		if (StringUtils.isNotBlank(strPesquisa) && (result == null || result.isEmpty() || !apenasSiglaSePossivel)) {
			LinkedHashSet<AghEspecialidades> li = new LinkedHashSet<AghEspecialidades>(result);
			List<AghEspecialidades> objList = getAghEspecialidadesDAO()
					.pesquisaEspecialidadesAtivasNaoInternasPorNome(strPesquisa, 25);
			li.addAll(objList);

			result = new ArrayList<AghEspecialidades>(li);
		}
		return result;
	}

	private List<AghEspecialidades> executarPesquisaEspecialidadesSolicitacaoInternacao(Object paramPesquisa,
			boolean apenasSiglaSePossivel) {
		AghEspecialidadesDAO aghEspecialidadesDAO = this.getAghEspecialidadesDAO();
		
		List<AghEspecialidades> lista = aghEspecialidadesDAO.listarEspecialidadesPorSiglaUsandoLike((String) paramPesquisa, 2);
		if (lista != null && lista.size() == 1) {
			return lista;
		} else {
			String strPesquisa = (String) paramPesquisa;
	
			List<AghEspecialidades> result = aghEspecialidadesDAO.pesquisaEspecialidadesInternasPorSiglaFaixaIdade(strPesquisa, null, 25);
	
			// Se tem o que pesquisar vai por descricao tambem mantendo a ordem da
			// pesquisa anterior sem repetir registros
			if (StringUtils.isNotBlank(strPesquisa) && (result == null || result.isEmpty() || !apenasSiglaSePossivel)) {
				LinkedHashSet<AghEspecialidades> li = new LinkedHashSet<AghEspecialidades>(result);
				List<AghEspecialidades> objList = aghEspecialidadesDAO.pesquisaEspecialidadesInternasPorNomeFaixaIdade(strPesquisa, null, 25);
				li.addAll(objList);
	
				result = new ArrayList<AghEspecialidades>(li);
			}
			return result;
		}
	}
	
	private List<AghEspecialidades> executarPesquisaEspecialidadesSolicitacaoInternacao(
			Object paramPesquisa,
			boolean apenasSiglaSePossivel, Integer idade) {

		String strPesquisa = (String) paramPesquisa;

		List<AghEspecialidades> result = getAghEspecialidadesDAO().pesquisaEspecialidadesInternasPorSiglaFaixaIdade(strPesquisa,
				idade.shortValue(), null);

		// Se tem o que pesquisar vai por descricao tambem mantendo a ordem da
		// pesquisa anterior sem repetir registros
		if (StringUtils.isNotBlank(strPesquisa) && (result == null || result.isEmpty() || !apenasSiglaSePossivel)) {
			LinkedHashSet<AghEspecialidades> li = new LinkedHashSet<AghEspecialidades>(result);

			List<AghEspecialidades> objList = getAghEspecialidadesDAO().pesquisaEspecialidadesInternasPorNomeFaixaIdade(strPesquisa,
					idade.shortValue(), null);
			li.addAll(objList);

			result = new ArrayList<AghEspecialidades>(li);
		}

		return result;
	}

	private List<AghEspecialidades> executarPesquisaEspecialidades(Object paramPesquisa, boolean verificarIndConsultoria,
			boolean apenasSiglaSePossivel, boolean apenasAtivas) {

		// Retira os caracteres especiais e espaços em branco desnecessários,
		// pesquisa permissiva no esquema do Google e protegida de SQL
		// Injection.
		// Foi removido a pedido da Gabriela, é melhor deixar falhar com
		// caracteres errados, tem só de substituir o % por outra coisa.
		// String strPesquisa =
		// StringCleaner.mantemLetras((String)paramPesquisa);
		// strPesquisa = strPesquisa.trim();

		String strPesquisa = (String) paramPesquisa;

		List<AghEspecialidades> result = null;

		if (CoreUtil.isNumeroShort(strPesquisa)) {
			result = getAghEspecialidadesDAO().pesquisaEspecialidadesPorSeq(Short.valueOf(strPesquisa), verificarIndConsultoria,
					apenasSiglaSePossivel, apenasAtivas, 100);
		}

		if ((result == null || result.isEmpty() || result.size() > 1) && StringUtils.isNotBlank(strPesquisa)) {
			strPesquisa = strPesquisa.trim();
			strPesquisa = strPesquisa.replace('%', '#');
			String sigla = strPesquisa.toUpperCase();
			result = getAghEspecialidadesDAO().pesquisaEspecialidadesPorSigla(sigla, verificarIndConsultoria, apenasSiglaSePossivel,
					apenasAtivas, 100);
		}

		// Se tem o que pesquisar vai por descricao tambem mantendo a ordem da
		// pesquisa anterior sem repetir registros
		if (StringUtils.isNotBlank(strPesquisa)) {
			if ((result == null || result.isEmpty()) && !apenasSiglaSePossivel) {
				LinkedHashSet<AghEspecialidades> li = new LinkedHashSet<AghEspecialidades>(result);

				List<AghEspecialidades> objList = getAghEspecialidadesDAO().pesquisaEspecialidadesPorNome(strPesquisa,
						verificarIndConsultoria, apenasSiglaSePossivel, apenasAtivas, 100);

				li.addAll(objList);
				result = new ArrayList<AghEspecialidades>(li);
			}
		} else {
			result = getAghEspecialidadesDAO().pesquisaEspecialidades(verificarIndConsultoria, apenasSiglaSePossivel, apenasAtivas,
					100);
		}

		return result;
	}

	/**
	 * Lista especialidades pela siga ou descricao ordenando os resultados pela sigla ou descricao.
	 * @param paramPesquisa - sigla ou descricao
	 * @return
	 */
	public List<AghEspecialidades> listarEspecialidadesPorSiglaOuDescricao(Object paramPesquisa, boolean ordemPorSigla){
		
		return listarEspecialidadesPorSiglaOuDescricao(paramPesquisa, ordemPorSigla, false);
	}
	
	public List<AghEspecialidades> listarEspecialidadesSolicitacaoProntuario(Object paramPesquisa, boolean ordemPorSigla){
		return listarEspecialidadesSolicitacaoProntuario(paramPesquisa, ordemPorSigla, true);
	}
	
	public Long listarEspecialidadesSolicitacaoProntuarioCount(Object paramPesquisa, boolean ordemPorSigla){
		return listarEspecialidadesSolicitacaoProntuarioCount(paramPesquisa, ordemPorSigla, true);
	}
	
	public List<AghEspecialidades> listarEspecialidadesSolicitacaoProntuario(Object paramPesquisa, boolean ordemPorSigla, boolean apenasAtivas){
		String strPesquisa = (String) paramPesquisa;
		return getAghEspecialidadesDAO().listarEspecialidadesSolicitacaoProntuario(strPesquisa, ordemPorSigla, apenasAtivas, 200);
	}
	
	public Long listarEspecialidadesSolicitacaoProntuarioCount(Object paramPesquisa, boolean ordemPorSigla, boolean apenasAtivas){
		return getAghEspecialidadesDAO().listarEspecialidadesSolicitacaoProntuarioCount((String) paramPesquisa, apenasAtivas);
	}

	/**
	 * Lista especialidades ATIVAS pela siga ou descricao ordenando os resultados pela sigla ou descricao.
	 * @param paramPesquisa - sigla ou descricao
	 * @return
	 */
	public List<AghEspecialidades> listarEspecialidadesAtivasPorSiglaOuDescricao(Object paramPesquisa, boolean ordemPorSigla){
		
		return listarEspecialidadesPorSiglaOuDescricao(paramPesquisa, ordemPorSigla, true);
	}

	
	public List<AghEspecialidades> listarEspecialidadesPorSiglaOuDescricao(Object paramPesquisa, boolean ordemPorSigla, boolean apenasAtivas){

		String strPesquisa = (String) paramPesquisa;
		
		List<AghEspecialidades> result = getAghEspecialidadesDAO().listarEspecialidadesPorSigla(strPesquisa, ordemPorSigla,
				apenasAtivas, 25);

		// Se tem o que pesquisar vai por descricao tambem mantendo a ordem da
		// pesquisa anterior sem repetir registros
		if (StringUtils.isNotBlank(strPesquisa) && (result == null || result.isEmpty())) {
			LinkedHashSet<AghEspecialidades> li = new LinkedHashSet<AghEspecialidades>(result);

			List<AghEspecialidades> objList = getAghEspecialidadesDAO().listarEspecialidadesPorNome(strPesquisa, ordemPorSigla,
					apenasAtivas, 25);
			
			li.addAll(objList);

			result = new ArrayList<AghEspecialidades>(li);
		}
		
		return result;
	}
	
	public List<AghEspecialidades> pesquisarPorNomeOuSiglaExata(String parametro){
		return this.getAghEspecialidadesDAO().pesquisarPorNomeOuSiglaEspSeqNuloAtivos(parametro);
	}
	
	public RapPessoasFisicas obterEspecialidadeInternacaoServidorChefePessoaFisica(final Short seq){
		return getAghEspecialidadesDAO().obterEspecialidadeInternacaoServidorChefePessoaFisica(seq);
	}
	
	protected AghEspecialidadesDAO getAghEspecialidadesDAO(){
		return aghEspecialidadesDAO;
	}
}
