package br.gov.mec.aghu.exames.ejb;

import javax.ejb.Local;

import br.gov.mec.aghu.estoque.vo.VoltarProtocoloUnicoVO;
import br.gov.mec.aghu.model.LwsComunicacao;
import br.gov.mec.aghu.core.exception.BaseException;

@Local
public interface VoltarProtocoloUnicoBeanLocal {

	void processarComunicacaoModuloGestaoAmostra(String nomeMicrocomputador) throws BaseException;
	
	void voltarProtocoloUnico(LwsComunicacao lwsComunicacao, VoltarProtocoloUnicoVO voVariaveisPacote, String nomeMicrocomputador) throws BaseException;

	LwsComunicacao inserirComErro(final Integer seqComunicacao, final String observacao) throws BaseException;
	
	LwsComunicacao obterRecepcaoResultadoGestaoAmostra();

	void atualizarResultadoGestaoAmostra(Integer idComunicacao) throws BaseException;
	
	void inserirLwsComunicacaoProcessadaSucesso(Integer seqComunicacao) throws BaseException;
	
	void inserirLwsComunicacaoProcessadaRestricoes(Integer seqComunicacao, VoltarProtocoloUnicoVO voVariaveisPacote) throws BaseException;
	
}


