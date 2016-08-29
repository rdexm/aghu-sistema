package br.gov.mec.aghu.blococirurgico.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.SalaCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.TipoAnestesiaVO;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

/**
 * Service de Bloco Cirurgico
 * 
 * @author luismoura
 * 
 */
@Local
@Deprecated
public interface IBlocoCirurgicoService {

	/**
	 * Pesqusia salas cirurgicas ativas por SEQ e/ou NOME
	 * 
	 * Web Service #38100
	 * 
	 * @param unfSeq
	 * @param seqp
	 * @param nome
	 * @return
	 */
	List<SalaCirurgicaVO> obterSalasCirurgicasAtivasPorUnfSeqNome(final Short unfSeq, final Short seqp, final String nome);

	/**
	 * Count de salas cirurgicas ativas por SEQ e/ou NOME
	 * 
	 * Web Service #38100
	 * 
	 * @param unfSeq
	 * @param seqp
	 * @param nome
	 * @return
	 */
	Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(final Short unfSeq, final Short seqp, final String nome);

	/**
	 * Obter os dados de uma sala cirúrgica para determinada unidade funcional filtrando por seqp
	 * 
	 * @param unfSeq
	 * @param seqp
	 * @return
	 */
	SalaCirurgicaVO obterDadosSalaCirurgica(final Short unfSeq, final Short seqp);

	/**
	 * Buscar os dados de anestesia sendo informado ou o código ou a descrição da anestesia.
	 * 
	 * Web Service #38221
	 * 
	 * @param seq
	 * @param descricao
	 * @return
	 */
	List<TipoAnestesiaVO> pequisarTiposAnestesiasAtivas(final Short seq, final String descricao);

	/**
	 * Buscar os dados de anestesia sendo informado ou o código ou a descrição da anestesia.
	 * 
	 * Web Service #38221
	 * 
	 * @param seq
	 * @param descricao
	 * @return
	 */
	Long pequisarTiposAnestesiasAtivasCount(final Short seq, final String descricao);
	
	String obterPendenciaFichaAnestesia(Integer atdSeq);
	
	/**
	 * Web Service #38487
	 * Utilizado na estória #26325
	 * @param pacCodigo
	 * @param listaUnfSeq
	 * @param dataCirurgia
	 * @return
	 */
	List<Object[]> obterCirurgiaDescCirurgicaPaciente(Integer pacCodigo, List<Short> listaUnfSeq, Date dataCirurgia);
	
	/**
	 * Web Service #37435
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	Object[] obterConselhoESiglaVRapServidorConselho(Integer matricula, Short vinCodigo);
	
	List<MbcCirurgiaVO> obterCirurgiasPorPacienteEDatas(List<Date> datas, Integer pacCodigo, List<Short> listaSeqp);
	
	void inserirCirurgiaDoCentroObstetrico(Integer pacCodigo
			,Short gestacaoSeqp
			,String nivelContaminacao
			,Date dataInicioProcedimento
			,Short salaCirurgicaSeqp
			,Short tempoDuracaoProcedimento
			,Short anestesiaSeqp
			,Short equipeSeqp
			,String tipoNascimento) throws ServiceException, ServiceBusinessException;
	
	/**
	 * Web Service #39545
	 * @return
	 */
	Boolean verificarExisteDescricaoCirurgica(Integer crgSeq);

	/**
	 * Web Service #39546
	 * @return
	 */
	Boolean verificarExisteDescricaoCirurgicaPorCirurgiaEProfissional(Integer crgSeq, Integer servidorMatricula, Short servidorVinCodigo);

}
