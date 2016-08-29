package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghSamis;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipCidadesDistritoSanitario;
import br.gov.mec.aghu.model.AipDistritoSanitarios;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.model.AipPaises;
import br.gov.mec.aghu.model.AipSinonimosOcupacao;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.model.AipTituloLogradouros;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.AipOcupacoesVO;
import br.gov.mec.aghu.paciente.vo.LogradouroVO;
import br.gov.mec.aghu.paciente.vo.ProfissaoVO;

public interface ICadastrosBasicosPacienteFacade extends Serializable {

	AipBairros obterBairro(Integer codigo);

	List<AipBairros> pesquisarBairro(Integer firstResult, Integer maxResults,
			Integer codigo, String descricao);

	
	void persistirBairro(AipBairros bairro) throws ApplicationBusinessException;

	List<AipBairros> pesquisarBairro(Object objPesquisa);

	Long obterBairroCount(Integer codigo, String descricao);

	
	void excluirBairro(Integer aipBairroCodigo) throws ApplicationBusinessException;

	AipCidades obterCidade(Integer codigoCidade);

	AipCidades obterCidadePorCodigo(Integer codigo,
			boolean obterDistritosSanitarios);

	AipCidades obterCidadePorCodigo(Integer codigo);
	
	AipCidades obterCidadePorChavePrimaria(Integer codigo);
	
	void excluirCidade(Integer codigo) throws ApplicationBusinessException;

	Long obterCidadeCount(Integer codigo, Integer codIbge, String nome,
			DominioSituacao situacao, Integer cep, Integer cepFinal, AipUfs uf);

	/**
	 * Método para pesquisar cidades que contemplam restrições informadas pelo usuário na tela de pesquisa de cidades.
	 *  
	 * @return Lista de cidades
	 */
	List<AipCidades> pesquisarCidades(Integer firstResult, Integer maxResult,
			Integer codigo, Integer codIbge, String nome,
			DominioSituacao situacao, Integer cep, Integer cepFinal, AipUfs uf);

	List<AipCidades> pesquisarCidades(Integer codigo, Integer codIbge,
			String nome, DominioSituacao situacao, Integer cep,
			Integer cepFinal, AipUfs uf);

	List<AipCidades> pesquisarCidades(Object objPesquisa);

	List<AipCidades> pesquisarPorCodigoNome(Object paramPesquisa, boolean ativas);

	List<AipCidades> pesquisarCidadePorCodigoNome(String strPesquisa);
	
	Long pesquisarCidadePorCodigoNomeCount(String strPesquisa);

	long pesquisarCountCidadePorCodigoNome(String strPesquisa);

	
	List<AipDistritoSanitarios> pesquisarDistritoSanitariosPorIds(
			AipCidades cidade);

	
	void persistirCidade(AipCidades cidade, List<AipDistritoSanitarios> distritos) throws ApplicationBusinessException;

	List<AipCidades> pesquisarCidadePorNome(Object strPesquisa);

	AipUfs obterUF(String aipSiglaUF);

	
	void salvarUF(AipUfs uf) throws ApplicationBusinessException;

	
	void alterarUF(AipUfs uf) throws ApplicationBusinessException;

	
	void removerUF(String sigla) throws ApplicationBusinessException;

	List<AipPaises> pesquisarPaisesPorDescricao(String strPesquisa);

	List<AipUfs> pesquisaUFs(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc, String sigla, String nome,
			String siglaPais, DominioSimNao cadastraCidades);
	
	List<AipUfs> listarTodosUFs();
	
	Long pesquisaUFsCount(String sigla, String nome, String siglaPais,
			DominioSimNao cadastraCidades);

	List<AipUfs> pesquisarPorSiglaNome(Object paramPesquisa);

	List<AipUfs> pesquisarPorSiglaNomePermiteCidades(Object paramPesquisa);

	List<AipUfs> pesquisarPorSiglaNomeSemLike(Object paramPesquisa);

	Long obterCountUfPorSiglaNome(Object paramPesquisa);

	List<AipUfs> pesquisarPorSiglaEntaoNome(String valor);

	Long pesquisarPaisesPorDescricaoCount(String strPesquisa);

	AipUfs obterUfSemLike(String aipSiglaUF);

	AipLogradouros obterLogradouroPorCodigo(Integer codigo);

	
	void persistirLogradouro(AipLogradouros aipLogradouro)
			throws ApplicationBusinessException;

	void cancelaEdicaoOuInclusaoLogradouro(AipLogradouros logradouro);

	List<AipTipoLogradouros> pesquisarTipoLogradouro(Object descricao);

	List<AipTituloLogradouros> pesquisarTituloLogradouro(Object descricao);

	List<AipCidades> pesquisarCidadesParaLogradouro(Object objPesquisa);

	Long obterLogradouroVOCount(String nome, AipCidades aipCidades);

	List<LogradouroVO> pesquisarLogradouroVO(Integer firstResult,
			Integer maxResults, String nome, AipCidades aipCidades);

	
	void excluirLogradouro(Integer codigoLogradouro)
			throws ApplicationBusinessException;

	
	void excluirBairroCepLogradouro(Integer codigoLogradouro, Integer cep,
			Integer codigoBairro) throws ApplicationBusinessException;

	
	void persistirTipoLogradouro(AipTipoLogradouros tipoLogradouro)
			throws ApplicationBusinessException;

	AipTipoLogradouros obterTipoLogradouroPorCodigo(Integer codigo);

	Long obterTipoLogradouroCount(String abreviatura, String descricao);

	List<AipTipoLogradouros> pesquisarTipoLogradouro(Integer firstResult,
			Integer maxResult, String abreviatura, String descricao);

	
	void removerTipoLogradouro(AipTipoLogradouros tipoLogradouro)
			throws ApplicationBusinessException;

	AipTituloLogradouros obterTituloLogradouroPorCodigo(Integer codigo);

	
	void persistirTituloLogradouro(AipTituloLogradouros tituloLogradouro)
			throws ApplicationBusinessException;

	Long obterTituloLogradouroCount(Integer codigo, String descricao);

	List<AipTituloLogradouros> pesquisarTituloLogradouro(Integer firstResult,
			Integer maxResult, Integer codigo, String descricao);

	
	void removerTituloLogradouro(AipTituloLogradouros tituloLogradouro)
			throws ApplicationBusinessException;

	
	AipDistritoSanitarios obterDistritoSanitarioPorCodigo(Short codigo);
	
	AipDistritoSanitarios obterDistritoSanitarioPorCodigo(Short codigo, Enum ...enums);

	Long obterDistritoSanitarioCount(Short codigo, String descricao);

	List<AipDistritoSanitarios> pesquisarDistritoSanitario(Integer firstResult,
			Integer maxResult, Short codigo, String descricao);

	
	void persistDistritoSanitario(AipDistritoSanitarios distritoSanitario, List<AipCidadesDistritoSanitario> cidadesInseridas, List<AipCidadesDistritoSanitario> cidadesExcluidas) throws ApplicationBusinessException;

	
	void removerDistritoSanitario(AipDistritoSanitarios distritoSanitario)
			throws ApplicationBusinessException;

	List<AipDistritoSanitarios> pesquisarDistritoSanitariosPorCodigoDescricao(
			Object objPesquisa);

	Long obterPaisCount(String sigla, String nome);

	List<AipPaises> pesquisarPais(Integer firstResult, Integer maxResult,
			String sigla, String nome);

	AipPaises obterPaisPorSigla(String sigla);

	
	void removerPais(String sigla) throws ApplicationBusinessException;

	
	void persistirPais(AipPaises pais, boolean edicao)
			throws ApplicationBusinessException;

	AipPaises obterPaisPorNome(String nome);

	List<AipCepLogradouros> listarCepLogradourosPorCEP(Integer cep);

	Long pesquisaNacionalidadesCount(Integer codigo, String sigla,
			String descricao, DominioSituacao situacao);

	List<AipNacionalidades> pesquisaNacionalidades(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String sigla, String descricao,
			DominioSituacao situacao);

	AipNacionalidades obterNacionalidade(Integer codigo);

	
	void excluirNacionalidade(AipNacionalidades nacionalidade)
			throws ApplicationBusinessException;

	
	void incluirNacionalidade(AipNacionalidades nacionalidade)
			throws ApplicationBusinessException;

	
	void atualizarNacionalidade(AipNacionalidades nacionalidade)
			throws ApplicationBusinessException;

	List<AipNacionalidades> pesquisarNacionalidadesInclusiveInativasPorCodigoSiglaNome(
			Object paramPesquisa);

	List<AipNacionalidades> pesquisarPorCodigoSiglaNome(String paramPesquisa);

	Long pesquisarCountPorCodigoSiglaNome(Object paramPesquisa);

	List<AipNacionalidades> pesquisarNacionalidadesPessoaFisica(String valor);

	List<Integer> listarLogradouros(Integer cep,
			Integer codigoTipoLogradouro);


	AipOcupacoes obterOcupacao(Integer codigo);
	AipOcupacoes obterOcupacao(Integer codigo, boolean left, Enum ...fields);
	
	List<AipSinonimosOcupacao> pesquisarSinonimosPorOcupacao(AipOcupacoes ocupacao);

	void removerOcupacao(Integer codigo) throws ApplicationBusinessException;

	Long pesquisaOcupacoesCount(Integer codigo, String descricao);

	
	List<AipOcupacoesVO> pesquisarOcupacoes(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String descricao);

	void validarSinonimo(AipSinonimosOcupacao sinonimo,
			List<AipSinonimosOcupacao> sinonimos, AipOcupacoes ocupacao)
			throws ApplicationBusinessException;

	
	void persistirOcupacao(AipOcupacoes ocupacao, final List<AipSinonimosOcupacao> sinonimosOcupacaoInseridos, final List<AipSinonimosOcupacao> sinonimosOcupacaoRemovidos) throws ApplicationBusinessException;

	List<AipOcupacoes> pesquisarPorCodigoDescricao(Object paramPesquisa);

	List<ProfissaoVO> pesquisarProfissioesPorCodigoDescricao(String strPesquisa);

	Long pesquisarProfissioesPorCodigoDescricaoCount(String strPesquisa);

	Long pesquisaCount(Integer codigo, String descricao,
			DominioSituacao situacao);

	AipOcupacoes obterAipOcupacoesPorChavePrimaria(Integer codigo);
	
	List<AipFinalidadesMovimentacao> pesquisa(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer codigo, String descricao, DominioSituacao situacao);

	
	AipFinalidadesMovimentacao obterFinalidadeMovimentacao(
			Integer aipFinalidadeMovimentacaoCodigo);

	
	void excluirFinalidadeMovimentacao(
			AipFinalidadesMovimentacao finalidadesMovimentacao)
			throws ApplicationBusinessException;

	
	void persistirFinalidadeMovimentacao(
			AipFinalidadesMovimentacao finalidadeMovimentacao)
			throws ApplicationBusinessException;

	/**
	 * @dbtables AipFinalidadesMovimentacao select
	 * 
	 * @param objPesquisa
	 * @return
	 */
	
	List<AipFinalidadesMovimentacao> pesquisarFinalidadeMovimentacaoPorCodigoEDescricao(
			Object objPesquisa);

	Long pesquisaFinalidadeMovimentacaoCount(Integer codigo, String descricao);

	
	List<AipFinalidadesMovimentacao> pesquisaFinalidadesMovimentacao(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer codigo, String descricao,
			DominioSituacao situacao);

	List<AipDistritoSanitarios> pesquisarDistritoSanitarioPorCidadeCodigo(Integer cddCodigo);
	
	AipSolicitantesProntuario obterSolicitanteProntuario(
			Short aipSolicitanteProntuarioCodigo);

	Long obterSolicitanteProntuarioCount(Integer seq,
			DominioSituacao indSituacao,
			AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento,
			AipFinalidadesMovimentacao finalidadeMovimentacao,
			String descricao, Boolean exigeResponsavel,
			DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia,
			DominioSimNao retorno, DominioTodosUltimo volumesManuseados);

	
	List<AipSolicitantesProntuario> pesquisarSolicitanteProntuario(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Integer seq, DominioSituacao indSituacao,
			AghUnidadesFuncionais unidadeFuncional,
			AghOrigemEventos origemEvento,
			AipFinalidadesMovimentacao finalidadeMovimentacao,
			String descricao, Boolean exigeResponsavel,
			DominioSimNao mensagemSamis, DominioSimNao separacaoPrevia,
			DominioSimNao retorno, DominioTodosUltimo volumesManuseados);

	
	void persistirSolicitanteProntuario(
			AipSolicitantesProntuario solicitanteProntuario)
			throws ApplicationBusinessException;
	
	List<AipCidadesDistritoSanitario> obterAipCidadesDistritoSanitarioPorAipDistritoSanitario(Short dstCodigo);
	
	List<AipCidades> pesquisarPorCodigoNomeAlfabetica(Object paramPesquisa,
			boolean ativas);

	/* ### Fim Finalidade Movimentação ### */

	//Grava o Samis Origem do Prontuário (setor de arquivamento do prontuário)

	void persistirOrigemProntuario(AghSamis samisOrigemProntuario,
			RapServidores servidorLogado) throws ApplicationBusinessException;

	Long pesquisaOrigemProntuarioCount(
			Integer codigoPesquisaOrigemProntuario, String descricaoPesquisa,
			DominioSituacao situacaoOrigemProntuario);

	List<AghSamis> pesquisaOrigemProntuario(Integer firstResult,
			Integer maxResults, String string, boolean b,
			Integer codigoPesquisaOrigemProntuario, String descricaoPesquisa,
			DominioSituacao situacaoOrigemProntuario);

	AghSamis obterOrigemProntuario(Short codigo);

	void excluirOrigemProntuario(AghSamis aux, RapServidores servidorLogado, Short codigoSamisExclusao) throws ApplicationBusinessException;
	
	List<AghSamis> pesquisaOrigemProntuarioPorCodigoOuDescricao(String param);	
}