package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoGrupoControle;
import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpGrupoControle;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpLimiteItemControle;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public interface ICadastrosBasicosControlePacienteFacade extends
		Serializable {

	public Long pesquisarItensCount(String sigla, String descricao,
			EcpGrupoControle grupo, DominioSituacao situacao);

	public List<EcpItemControle> pesquisarItens(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String sigla,
			String descricao, EcpGrupoControle grupo, DominioSituacao situacao);

	public List<EcpItemControle> listarItensControleAtivos(
			EcpGrupoControle grupo);

	public List<EcpGrupoControle> listarGruposControleAtivos();

	/**
	 * Inseri um novo item de controle
	 * 
	 * @param ecpItemControle
	 * @throws BaseException
	 */
	
	public void inserir(EcpItemControle ecpItemControle)
			throws BaseException;

	/**
	 * Altera um item de controle
	 * 
	 * @param itemControle
	 * @throws ApplicationBusinessException
	 */
	
	public void alterar(EcpItemControle itemControle)
			throws ApplicationBusinessException;

	public List<EcpGrupoControle> pesquisarGruposControle(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, Integer seq,
			String descricao, Short ordem, DominioSituacao situacao , DominioTipoGrupoControle tipo);

	/**
	 * Insere um novo registro de grupo de controle
	 * 
	 * @param _grupoControle
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	
	public void inserirGrupoControle(EcpGrupoControle _grupoControle)
			throws ApplicationBusinessException;

	/**
	 * Altera um registro de grupo de controle
	 * 
	 * @param _grupoControle
	 * @throws ApplicationBusinessException
	 */
	
	public void alterarGrupoControle(EcpGrupoControle _grupoControle)
			throws ApplicationBusinessException;

	/**
	 * Excluir registro de grupo de controle
	 * 
	 * @param _grupoControle
	 * @throws ApplicationBusinessException
	 */
	public void excluirGrupoControle(Integer seq)
			throws ApplicationBusinessException;

	/**
	 * Busca a listagem de grupos de controle cadastrados.
	 * 
	 * @param _grupoControleSeq
	 * @param _descricao
	 * @return Lista contendo os grupos cadastrados
	 */
	public List<EcpGrupoControle> listarGruposControle(
			Integer _grupoControleSeq, String _descricao, Short _ordem,
			DominioSituacao _situacao, DominioTipoGrupoControle tipo);

	Long listarGruposControleCount(Integer _seq, String _descricao, Short _ordem, DominioSituacao _situacao, DominioTipoGrupoControle tipo);

	List<EcpItemControle> verificaReferenciaItemControle(EcpGrupoControle _grupo);

	
	void excluir(Short seq) throws ApplicationBusinessException;

	List<EcpLimiteItemControle> listarLimitesItemControle(Integer firstResult, Integer maxResult, String orderProperty,boolean asc, EcpItemControle itemControle);

	Long listarLimitesItemControleCount(EcpItemControle itemControle);

	EcpLimiteItemControle obterLimiteItemControle(Integer seqLimiteItemControle);

	void inserirLimiteItemControle(EcpLimiteItemControle ecpLimiteItemControle)throws ApplicationBusinessException;
	
	void alterarLimiteItemControle(EcpLimiteItemControle ecpLimiteItemControle) throws ApplicationBusinessException;

	
	void excluirLimiteItemControle(Integer seq)	throws ApplicationBusinessException;

	List<EpeCuidados> obterItensCuidadoEnfermagem(EcpItemControle itemControle);

	List<MpmCuidadoUsual> obterItensCuidadoMedico(EcpItemControle itemControle);

	EcpItemControle obterItemControle(Short seqItemControle, Enum[] fetchArgsInnerJoin, Enum[] fetchArgsLeftJoin);
	
	public void salvarAssociacaoCuidadosEnfermagem(
			EcpItemControle itemControle,
			List<EpeCuidados> listaCuidadosIncluir,
			List<EpeCuidados> listaCuidadosExcluir) throws BaseException;

	
	public void salvarAssociacaoCuidadosMedicos(EcpItemControle itemControle,
			List<MpmCuidadoUsual> listaCuidadosIncluir,
			List<MpmCuidadoUsual> listaCuidadosExcluir) throws BaseException;

	public EcpGrupoControle obterGrupoControle(Integer idGrupo);

	public List<EcpItemControle> buscarItensControlePorPacientePeriodo(
			AipPacientes paciente, Date dataInicial, Date dataFinal, Long trgSeq, DominioTipoGrupoControle... tipoGrupo);

	public EcpItemControle obterItemControlePorId(Short seq);

	public EcpLimiteItemControle buscaLimiteItemControle(EcpItemControle item,
			DominioUnidadeMedidaIdade medidaIdade, Integer idade);
	
}