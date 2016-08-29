package br.gov.mec.aghu.paciente.prontuarioonline.digitalizacao.business;

import java.util.List;

import javax.ejb.Local;

@Local
public interface IGEDService {

	List<DocumentoGEDVO> consultarDocumentosGED(String camposFicha, String usuarioGed, String senhaGed, Integer ficha);

}
