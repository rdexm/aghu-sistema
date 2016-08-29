package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.EpeCaractDefDiagnostico;
import br.gov.mec.aghu.model.EpeCaractDefDiagnosticoId;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeCuidadoDiagnosticoId;
import br.gov.mec.aghu.model.EpeCuidadoMedicamento;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnosticoId;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSinCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidoraId;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticosSinaisSintomasVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface IPrescricaoEnfermagemApoioFacade extends Serializable {
	
	
	Long pesquisarEtiologiasPorSeqDescricaoCount(Short seq, String descricao);
	
	List<EpeFatRelacionado> pesquisarEtiologiasPorSeqDescricao(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short seq, String descricao);	

	void removerEtiologia(Short seq) throws ApplicationBusinessException;

	void persistirDiagnostico(EpeDiagnostico diagnostico) throws ApplicationBusinessException;

	void excluirDiagnostico(EpeDiagnosticoId diagnosticoId) throws ApplicationBusinessException;		
	
	void persistirEtiologia(EpeFatRelacionado etiologia, Boolean ativo) throws ApplicationBusinessException;

	List<EpeGrupoNecesBasica> pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Short seqGrupoNecessidadesHumanas,
			String descricaoGrupoNecessidadesHumanas, DominioSituacao situacaoGrupoNecessidadesHumanas);

	Long pesquisarGrupoNecessidadesHumanasPorSeqDescricaoSituacaoCount(
			Short seqGrupoNecessidadesHumanas,
			String descricaoGrupoNecessidadesHumanas, DominioSituacao situacaoGrupoNecessidadesHumanas);

	void persistirGrupoNecessidadesHumanas(
			EpeGrupoNecesBasica grupoNecessidadesHumanas, Boolean ativo) throws ApplicationBusinessException;

	void removerGrupoNecessidadesHumanas(
			Short seq) throws ApplicationBusinessException;

	List<EpeSubgrupoNecesBasica> pesquisarSubgrupoNecessBasicaOrderSeq(
			Short gnbSeq);

	void removerSubgrupoNecessidadesHumanas(EpeSubgrupoNecesBasicaId id) throws ApplicationBusinessException;

	void persistirSubgrupoNecessidadesHumanas(
			EpeSubgrupoNecesBasica epeSubgrupoNecesBasica, Boolean checkboxSubGrupoAtivo, Short seqGrupoNecessidadesHumanas) throws ApplicationBusinessException;
	
	public EpeCaractDefinidora obterSinaisSintomasPorCodigo(Integer codigo);
	
	public void removerSinaisSintomas(Integer codigo);
	
	public List<EpeCaractDefinidora> listarSinaisSintomasPorCodigoDescricaoSituacao(Integer firstResult, Integer maxResult, String orderProperty, Boolean asc, Integer codigo, String descricao, DominioSituacao situacao);
	
	public Long listarSinaisSintomasPorCodigoDescricaoSituacaoCount(Integer codigo, String descricao, DominioSituacao situacao);
	
	public void persistirSinaisSintomas(EpeCaractDefinidora sinaisSintomas) throws ApplicationBusinessException;
	
	public void verificaExclusaoEpeCaractDefinidora(EpeCaractDefinidora epeCaractDefinidora) throws ApplicationBusinessException;
	
	public void verificaAlteracaoEpeCaractDefinidora(EpeCaractDefinidora epeCaractDefinidora) throws ApplicationBusinessException;

	List<EpeSinCaractDefinidora> buscarSinonimoSinaisSintomas(Integer cdeCodigo, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	Long buscarSinonimoSinaisSintomasCount(Integer cdeCodigo);

	List<EpeCaractDefinidora> pesquisarCaracteristicasDefinidoras(Object objSinaisSintomas, List<EpeSinCaractDefinidora> listaSinonimos);

	void inserirEpeSinCaractDefinidora(EpeSinCaractDefinidora novaCaracteristicaDefinidora) throws ApplicationBusinessException;
	
	public List<EpeFatRelacionado> pesquisarEtiologiasTodas(String parametro);
	
	public Long pesquisarEtiologiasCountTodas(String parametro);
	
	public EpeFatRelacionado obterEpeFatRelacionadoPorChavePrimaria(Short chavePrimaria);
	
	public void persistirCuidadoDiagnostico(EpeCuidadoDiagnostico cuidadodiagnostico) throws ApplicationBusinessException;
	
	public void atualizarCuidadoDiagnostico(EpeCuidadoDiagnostico cuidadodiagnostico) throws ApplicationBusinessException; 
	
	public void excluirCuidadoDiagnostico(EpeCuidadoDiagnosticoId id) throws ApplicationBusinessException;
	
	String removerEpeSinCaractDefinidora(EpeSinCaractDefinidoraId id);

	void persistirEtiologiaDiagnostico(EpeFatRelDiagnostico etiologiaDiagnostico) throws ApplicationBusinessException;

	void excluirEtiologiaDiagnostico(EpeFatRelDiagnosticoId id) throws ApplicationBusinessException;

	void ativarDesativarEtiologiaDiagnostico(EpeFatRelDiagnostico epeEtiologiaDiagnostico) throws ApplicationBusinessException;
		 
	
	List<AfaMedicamento> pesquisarMedicamentosParaMedicamentosCuidados(String parametro);
	
	Long pesquisarMedicamentosParaMedicamentosCuidadosCount(String parametro);

	List<AfaMedicamento> pesquisarMedicamentosParaListagemMedicamentosCuidados(Integer matCodigo, DominioSituacaoMedicamento indSituacao, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc);

	Long pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(Integer matCodigo, DominioSituacaoMedicamento codigoIndSituacao);
	
	AfaMedicamento obterMedicamentoPorMatCodigo(Integer matCodigo);
	
	void persistirCuidadoMedicamento(EpeCuidadoMedicamento cuidadoMedicamento) throws ApplicationBusinessException;
	
	void atualizarCuidadoMedicamento(EpeCuidadoMedicamento cuidadoMedicamento) throws ApplicationBusinessException;
	
	void excluirCuidadoMedicamento(Short cuiSeq, Integer matCodigo) throws ApplicationBusinessException;
	
	public List<DiagnosticosSinaisSintomasVO> obterDiagnosticosGrupoSubgrupoNecessidadesBasicas(Short gnbSeq, Short snbSequencia, Short dngSequencia, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	public Long obterDiagnosticosGrupoSubgrupoNecessidadesBasicasCount(Short gnbSeq, Short snbSequencia, Short dngSequencia);

	public List<EpeCaractDefDiagnostico> pesquisarCaractDefDiagnosticoPorSubgrupo(Short snbGnbSeq, Short snbSequencia, Short sequencia, Integer firstResult, Integer maxResult, String orderProperty, boolean asc);

	public Long pesquisarCaractDefDiagnosticoPorSubgrupoCount(Short snbGnbSeq, Short snbSequencia, Short sequencia);

	public List<EpeCaractDefinidora> pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnostico(Object filtro, Short snbGnbSeq, Short snbSequencia, Short sequencia);

	public Long pesquisarCaracteristicasDefinidorasNaoAtribuidasDiagnosticoCount(Object filtro, Short snbGnbSeq, Short snbSequencia, Short sequencia);

	public void excluirEpeCaractDefDiagnostico(EpeCaractDefDiagnosticoId id);

	public void inserirEpeCaractDefDiagnostico(EpeCaractDefDiagnostico epeCaractDefDiagnostico);

	List<EpeFatRelacionado> pesquisarEtiologiasNaoRelacionadas(String filtro, Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia);

	Long pesquisarEtiologiasNaoRelacionadasCount(String filtro, Short dgnSnbGnbSeq, Short dgnSnbSequencia, Short dgnSequencia);

}
